package com.github.nikitakuchur.chatmanager.repositories;

import com.github.nikitakuchur.chatmanager.models.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepository extends MongoRepository<Chat, String> {
    Page<Chat> findAllByOwner(String owner, Pageable pageable);
}
