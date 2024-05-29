package com.github.nikitakuchur.chatmanager.services;

import com.github.nikitakuchur.chatmanager.exceptions.ChatNotFoundException;
import com.github.nikitakuchur.chatmanager.exceptions.UnauthorizedChatDeleteException;
import com.github.nikitakuchur.chatmanager.exceptions.UnauthorizedChatEditException;
import com.github.nikitakuchur.chatmanager.jwt.JwtUser;
import com.github.nikitakuchur.chatmanager.models.Chat;
import com.github.nikitakuchur.chatmanager.models.Message;
import com.github.nikitakuchur.chatmanager.repositories.ChatRepository;
import com.github.nikitakuchur.chatmanager.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

/**
 * A service responsible for managing chats.
 */
@Service
@RequiredArgsConstructor
public class ChatService {
    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    /**
     * Creates a new chat.
     *
     * @param name          the name of the chat
     * @param description   the chat description
     * @param user          the user who is creating the chat
     * @return the ID of the created chat
     */
    public String createChat(String name, String description, JwtUser user) {
        Chat chat = chatRepository.save(Chat.builder()
                .name(name)
                .description(description)
                .owner(user.username())
                .build());
        return chat.getId();
    }

    /**
     * Updates the chat information.
     *
     * @param chatId            the chat ID
     * @param newName           the new name
     * @param newDescription    the new description
     * @param user              the user who is updating the chat
     * @throws UnauthorizedChatEditException if the user does not have permissions to edit the chat
     * @throws ChatNotFoundException if the chat does not exist
     */
    public void updateChat(String chatId, String newName, String newDescription, JwtUser user) {
        chatRepository.findById(chatId)
                .ifPresentOrElse(chat -> {
                    if (!ADMIN_ROLE.equals(user.role()) && !chat.getOwner().equals(user.username())) {
                        throw new UnauthorizedChatEditException("The user cannot update other people's chats.");
                    }
                    chat.setName(newName);
                    chat.setDescription(newDescription);
                    chatRepository.save(chat);
                }, () -> {
                    throw new ChatNotFoundException("The chat with id=" + chatId + " not found.");
                });
    }

    /**
     * Deletes the given chat.
     *
     * @param chatId    the chat ID
     * @param user      the user who is deleting the chat
     * @throws UnauthorizedChatEditException if the user does not have permissions to delete the chat
     * @throws ChatNotFoundException if the chat does not exist
     */
    public void deleteChat(String chatId, JwtUser user) {
        chatRepository.findById(chatId)
                .ifPresentOrElse(chat -> {
                    if (!ADMIN_ROLE.equals(user.role()) && !chat.getOwner().equals(user.username())) {
                        throw new UnauthorizedChatDeleteException("The user cannot delete other people's chats.");
                    }
                    chatRepository.delete(chat);
                    messageRepository.deleteAllByChatId(chatId);
                }, () -> {
                    throw new ChatNotFoundException("The chat with id=" + chatId + " not found.");
                });
    }

    /**
     * Finds all the chats that satisfy the given search phrase and have been created by the given user.
     * If the search phrase is null, it will be ignored, and the same applies to the owner.
     *
     * @param searchPhrase  the search phrase
     * @param owner         the username of the owner
     * @param pageable      pageable
     * @return a page of chats
     */
    public Page<Chat> search(String searchPhrase, String owner, Pageable pageable) {
        return chatRepository.search(searchPhrase, owner, pageable);
    }

    /**
     * Finds a chat by the given chat ID.
     *
     * @param chatId the chat ID
     * @return an Optional containing the chat, or empty if no chat is found
     */
    public Optional<Chat> getChat(String chatId) {
        return chatRepository.findById(chatId);
    }

    /**
     * Finds all the messages in the specified chat.
     *
     * @param chatId    the chat ID
     * @param startFrom the initial time
     * @param pageable  pageable
     * @return a page of messages
     */
    public Page<Message> getMessages(String chatId, Instant startFrom, Pageable pageable) {
        return messageRepository.findAllByChatId(chatId, startFrom, pageable);
    }

    /**
     * Saves the given list of messages to the database.
     *
     * @param message a list of messages to save
     */
    public void saveMessages(Iterable<Message> message) {
        messageRepository.saveAll(message);
    }
}
