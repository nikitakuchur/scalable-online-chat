package com.github.nikitakuchur.chatmanager.repositories;

import com.github.nikitakuchur.chatmanager.models.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class SearchableChatRepositoryImpl implements SearchableChatRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Chat> search(String searchPhrase, String owner, Pageable pageable) {
        Query query = new Query();
        if (StringUtils.hasText(searchPhrase)) {
            query.addCriteria(new TextCriteria().matching(searchPhrase));
        }
        if (StringUtils.hasText(owner)) {
            query.addCriteria(Criteria.where("owner").is(owner));
        }

        long count = mongoTemplate.count(query, Chat.class);
        List<Chat> chats = mongoTemplate.find(query.with(pageable), Chat.class);

        return new PageImpl<>(chats, pageable, count);
    }
}
