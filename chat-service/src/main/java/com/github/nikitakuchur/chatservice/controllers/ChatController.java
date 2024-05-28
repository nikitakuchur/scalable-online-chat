package com.github.nikitakuchur.chatservice.controllers;

import com.github.nikitakuchur.chatservice.jwt.JwtUser;
import com.github.nikitakuchur.chatservice.models.Message;
import com.github.nikitakuchur.chatservice.models.MessageDto;
import com.github.nikitakuchur.chatservice.models.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.user.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.time.Instant;
import java.util.UUID;

import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.DESTINATION_HEADER;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private static final String CHAT_SERVICE_SENDER = "chat-service";

    private final SimpUserRegistry simpUserRegistry;
    private final KafkaTemplate<String, Message> kafkaTemplate;

    @Value("${chat-service.kafka.message-topic}")
    private String messageTopic;

    @MessageMapping("/{chatId}")
    public void handleTextMessage(@DestinationVariable String chatId, Principal principal, MessageDto messageDto) {
        JwtUser jwtUser = getJwtUser(principal);

        Message message = new Message(
                UUID.randomUUID().toString(),
                chatId,
                jwtUser.username(),
                messageDto.text(),
                Instant.now(),
                MessageType.USER_MESSAGE
        );

        log.info("Sending a user message to Kafka. Message: {}", message);
        kafkaTemplate.send(messageTopic, message);
    }

    @EventListener
    private void handleSessionSubscribe(SessionSubscribeEvent event) {
        String destination = event.getMessage().getHeaders().get(DESTINATION_HEADER, String.class);
        if (destination == null) {
            return;
        }
        String chatId = destination.substring("/topic/".length());
        sendServiceMessage(event, chatId, "%s has joined the chat");
    }

    @EventListener
    @Order(Ordered.HIGHEST_PRECEDENCE)
    private void handleSessionDisconnect(SessionDisconnectEvent event) {
        Principal user = event.getUser();
        if (user == null) {
            return;
        }
        SimpUser simpUser = simpUserRegistry.getUser(user.getName());
        if (simpUser == null) {
            return;
        }
        for (SimpSession session : simpUser.getSessions()) {
            for (SimpSubscription subscription : session.getSubscriptions()) {
                String chatId = subscription.getDestination().substring("/topic/".length());
                sendServiceMessage(event, chatId, "%s has left the chat");
            }
        }
    }

    @SneakyThrows
    private void sendServiceMessage(AbstractSubProtocolEvent event, String chatId, String text) {
        JwtUser jwtUser = getJwtUser(event.getUser());

        Message message = new Message(
                UUID.randomUUID().toString(),
                chatId,
                CHAT_SERVICE_SENDER,
                String.format(text, jwtUser.username()),
                Instant.now(),
                MessageType.SERVICE_MESSAGE
        );

        log.info("Sending a service message to Kafka. Message: {}", message);
        kafkaTemplate.send(messageTopic, message);
    }

    private JwtUser getJwtUser(Principal principal) {
        if (principal == null) {
            throw new AccessDeniedException("The user is not specified.");
        }
        return (JwtUser) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
    }
}
