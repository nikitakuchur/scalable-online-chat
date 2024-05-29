package com.github.nikitakuchur.chatmanager.listeners;

import com.github.nikitakuchur.chatmanager.models.Message;
import com.github.nikitakuchur.chatmanager.services.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {

    private final ChatService chatService;

    @KafkaListener(topics = "${chat-manager.kafka.message-topic}", groupId = "${chat-manager.kafka.group-id}")
    public void listenChatMessages(List<Message> messages) {
        log.info("New messages have been received. Messages: {}", messages);
        chatService.saveMessages(messages);
    }
}
