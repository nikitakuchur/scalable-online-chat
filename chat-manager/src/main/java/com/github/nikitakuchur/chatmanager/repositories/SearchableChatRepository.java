package com.github.nikitakuchur.chatmanager.repositories;

import com.github.nikitakuchur.chatmanager.models.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchableChatRepository {
    Page<Chat> search(String searchPhrase, String owner, Pageable pageable);
}
