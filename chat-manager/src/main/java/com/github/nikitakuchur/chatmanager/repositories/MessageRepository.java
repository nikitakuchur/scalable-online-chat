package com.github.nikitakuchur.chatmanager.repositories;

import com.github.nikitakuchur.chatmanager.models.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;

public interface MessageRepository extends MongoRepository<Message, String> {
    @Query("{ chatId: ?0, timestamp: { $lt: ?1 } }")
    Page<Message> findAllByChatId(String chatId, Instant startFrom, Pageable pageable);
}
