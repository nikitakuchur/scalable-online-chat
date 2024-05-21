package com.github.nikitakuchur.chatmanager.rest;

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
    public Page<Chat> getChats(@RequestParam(required = false) String username, Pageable pageable) {
        if (username != null) {
            return chatService.getChatsByOwner(username, pageable);
        }
        return chatService.getAllChats(pageable);
    }

    @GetMapping("/chats/{id}/messages")
    public Page<Message> getMessages(@PathVariable String id, Pageable pageable) {
        return chatService.getMessages(id, pageable);
    }
}
