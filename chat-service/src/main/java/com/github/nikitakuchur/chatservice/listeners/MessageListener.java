package com.github.nikitakuchur.chatservice.listeners;

import com.github.nikitakuchur.chatservice.models.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "${chat-service.kafka.message-topic}", groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    public void listenChatMessages(Message message) {
        log.info("A new message has been received from Kafka. Message: {}", message);
        messagingTemplate.convertAndSend("/topic/" + message.chatId(), message);
    }
}
