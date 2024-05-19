package com.github.nikitakuchur.userservice.repositories;

import com.github.nikitakuchur.userservice.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    @Query("{ token: '?0' }")
    Optional<RefreshToken> findByToken(String token);

    @Query("{ sessionId: '?0' }")
    List<RefreshToken> findBySessionId(String sessionId);

    @Query("{ username: '?0' }")
    List<RefreshToken> findByUsername(String username);
}
