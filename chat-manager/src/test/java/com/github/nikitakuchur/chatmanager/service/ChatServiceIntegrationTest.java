package com.github.nikitakuchur.chatmanager.service;

import com.github.nikitakuchur.chatmanager.IntegrationTest;
import com.github.nikitakuchur.chatmanager.exceptions.ChatNotFoundException;
import com.github.nikitakuchur.chatmanager.exceptions.UnauthorizedChatDeleteException;
import com.github.nikitakuchur.chatmanager.exceptions.UnauthorizedChatEditException;
import com.github.nikitakuchur.chatmanager.jwt.JwtUser;
import com.github.nikitakuchur.chatmanager.models.Chat;
import com.github.nikitakuchur.chatmanager.models.Message;
import com.github.nikitakuchur.chatmanager.services.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class ChatServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void initDatabase() {
        mongoTemplate.findAllAndRemove(Query.query(new Criteria()), Chat.class);
        mongoTemplate.findAllAndRemove(Query.query(new Criteria()), Message.class);

        Chat chat1 = mongoTemplate.save(Chat.builder()
                .id("1")
                .name("Test chat #1")
                .description("This is a test chat #1.")
                .owner("user1")
                .build());
        generateMessages(chat1.getId(), 10);

        Chat chat2 = mongoTemplate.save(Chat.builder()
                .id("2")
                .name("Test chat #2")
                .description("This is a test chat #2.")
                .owner("user2")
                .build());
        generateMessages(chat2.getId(), 20);
    }

    private void generateMessages(String chatId, int n) {
        for (int i = 0; i < n; i++) {
            mongoTemplate.save(Message.builder()
                    .chatId(chatId)
                    .text("Test message #" + i)
                    .sender("user" + i)
                    .timestamp(Instant.now())
                    .build());
        }
    }

    @Test
    void createChatTest() {
        String chatName = "Test chat #3";
        String chatDescription = "This is a test chat #3";
        JwtUser jwtUser = new JwtUser("user", "ROLE_USER", "");

        String chatId = chatService.createChat(chatName, chatDescription, jwtUser);

        assertNotNull(chatId);

        Chat chat = mongoTemplate.findById(chatId, Chat.class);
        assertNotNull(chat);
        assertEquals(chatName, chat.getName());
        assertEquals(chatDescription, chat.getDescription());
        assertEquals(jwtUser.username(), chat.getOwner());
    }

    @Test
    void updateChatTest() {
        String newChatName = "Updated test chat #1";
        String newChatDescription = "Updated test chat #1";
        JwtUser jwtUser = new JwtUser("user1", "ROLE_USER", "");

        chatService.updateChat("1", newChatName, newChatDescription, jwtUser);

        Chat chat = mongoTemplate.findById("1", Chat.class);
        assertNotNull(chat);
        assertEquals(newChatName, chat.getName());
        assertEquals(newChatDescription, chat.getDescription());
    }

    @Test
    void updateChatWithNoPermissionsTest() {
        String newChatName = "Updated test chat #1";
        String newChatDescription = "Updated test chat #1";
        JwtUser jwtUser = new JwtUser("user2", "ROLE_USER", "");

        assertThrows(UnauthorizedChatEditException.class, () -> {
            chatService.updateChat("1", newChatName, newChatDescription, jwtUser);
        });
    }

    @Test
    void updateChatWithNonExistentChatTest() {
        String newChatName = "Updated test chat #1";
        String newChatDescription = "Updated test chat #1";
        JwtUser jwtUser = new JwtUser("user1", "ROLE_USER", "");

        assertThrows(ChatNotFoundException.class, () -> {
            chatService.updateChat("3", newChatName, newChatDescription, jwtUser);
        });
    }

    @Test
    void updateChatAsAdminTest() {
        String newChatName = "Updated test chat #1";
        String newChatDescription = "Updated test chat #1";
        JwtUser jwtUser = new JwtUser("user2", "ROLE_ADMIN", "");

        chatService.updateChat("1", newChatName, newChatDescription, jwtUser);

        Chat chat = mongoTemplate.findById("1", Chat.class);
        assertNotNull(chat);
        assertEquals(newChatName, chat.getName());
        assertEquals(newChatDescription, chat.getDescription());
    }

    @Test
    void deleteChatTest() {
        JwtUser jwtUser = new JwtUser("user1", "ROLE_USER", "");

        chatService.deleteChat("1", jwtUser);

        Chat chat = mongoTemplate.findById("1", Chat.class);
        assertNull(chat);
    }

    @Test
    void deleteChatWithNoPermissionsTest() {
        JwtUser jwtUser = new JwtUser("user2", "ROLE_USER", "");

        assertThrows(UnauthorizedChatDeleteException.class, () -> {
            chatService.deleteChat("1", jwtUser);
        });
    }

    @Test
    void deleteChatWithNonExistentChatTest() {
        JwtUser jwtUser = new JwtUser("user1", "ROLE_USER", "");

        assertThrows(ChatNotFoundException.class, () -> {
            chatService.deleteChat("3", jwtUser);
        });
    }

    @Test
    void deleteChatAsAdminTest() {
        JwtUser jwtUser = new JwtUser("user2", "ROLE_ADMIN", "");

        chatService.deleteChat("1", jwtUser);

        Chat chat = mongoTemplate.findById("1", Chat.class);
        assertNull(chat);
    }

    @Test
    void getAllChatsTest() {
        Page<Chat> allChats = chatService.getAllChats(PageRequest.ofSize(10));

        assertNotNull(allChats);
        assertEquals(2, allChats.getTotalElements());
    }

    @Test
    void getUserChatsTest() {
        Page<Chat> userChats = chatService.getChatsByOwner("user1", PageRequest.ofSize(10));

        assertNotNull(userChats);
        assertEquals(1, userChats.getTotalElements());
    }

    @Test
    void getMessagesChatsTest() {
        Page<Message> messages = chatService.getMessages("1", PageRequest.ofSize(10));

        assertNotNull(messages);
        assertEquals(10, messages.getTotalElements());
    }
}
