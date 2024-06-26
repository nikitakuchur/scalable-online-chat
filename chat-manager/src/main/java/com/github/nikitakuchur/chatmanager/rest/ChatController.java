package com.github.nikitakuchur.chatmanager.rest;

import com.github.nikitakuchur.chatmanager.exceptions.ChatNotFoundException;
import com.github.nikitakuchur.chatmanager.jwt.JwtUser;
import com.github.nikitakuchur.chatmanager.models.Chat;
import com.github.nikitakuchur.chatmanager.models.Message;
import com.github.nikitakuchur.chatmanager.models.dto.ChatInfoRequest;
import com.github.nikitakuchur.chatmanager.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chats")
    public String createChat(@RequestBody @Validated ChatInfoRequest request, @AuthenticationPrincipal JwtUser jwtUser) {
        return chatService.createChat(request.getName(), request.getDescription(), jwtUser);
    }

    @PostMapping("/chats/{id}")
    public void updateChat(@PathVariable String id,
                           @RequestBody @Validated ChatInfoRequest request,
                           @AuthenticationPrincipal JwtUser jwtUser) {
        chatService.updateChat(id, request.getName(), request.getDescription(), jwtUser);
    }

    @DeleteMapping("/chats/{id}")
    public void deleteChat(@PathVariable String id, @AuthenticationPrincipal JwtUser jwtUser) {
        chatService.deleteChat(id, jwtUser);
    }

    @GetMapping("/chats")
    public Page<Chat> search(String searchPhrase, String owner, Pageable pageable) {
        return chatService.search(searchPhrase, owner, pageable);
    }

    @GetMapping("/chats/{id}")
    public Chat getChat(@PathVariable String id) {
        return chatService.getChat(id)
                .orElseThrow(() -> new ChatNotFoundException("The chat with id=" + id + " not found."));
    }

    @GetMapping("/chats/{id}/messages")
    public Page<Message> getMessages(@PathVariable String id, Instant startFrom, Pageable pageable) {
        if (startFrom == null) {
            startFrom = Instant.now();
        }
        return chatService.getMessages(id, startFrom, pageable);
    }
}
