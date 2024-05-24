package com.github.nikitakuchur.chatservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nikitakuchur.chatservice.jwt.JwtUser;
import com.github.nikitakuchur.chatservice.models.Message;
import com.github.nikitakuchur.chatservice.models.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @MessageMapping("/{chatId}")
    protected void handleTextMessage(@DestinationVariable String chatId, Principal principal, @AuthenticationPrincipal JwtUser user, MessageDto messageDto) throws JsonProcessingException {
        JwtUser jwtUser = (JwtUser) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        Message message = new Message(
                UUID.randomUUID().toString(),
                chatId,
                jwtUser.username(),
                messageDto.text(),
                Instant.now()
        );

        log.info(message.toString());

        String kafkaMessage = objectMapper.writeValueAsString(message);
        kafkaTemplate.send("chat-messages", kafkaMessage);
    }
}
