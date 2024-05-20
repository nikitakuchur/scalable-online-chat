package com.github.nikitakuchur.userservice.repositories;

import com.github.nikitakuchur.userservice.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByUsername(String username);

    void deleteAllBySessionId(String sessionId);

    void deleteAllByUsername(String username);
}
