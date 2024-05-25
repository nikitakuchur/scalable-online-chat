package com.github.nikitakuchur.chatmanager.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nikitakuchur.chatmanager.models.Message;
import com.github.nikitakuchur.chatmanager.services.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {

    private final ObjectMapper objectMapper;
    private final ChatService chatService;

    @KafkaListener(topics = "${chat-manager.kafka.message-topic}", groupId = "${chat-manager.kafka.group-id}")
    public void listenChatMessages(String data) throws JsonProcessingException {
        Message message = objectMapper.readValue(data, Message.class);
        log.info("A new message has been received. Message: {}", message);
        chatService.saveMessage(message);
    }
}
