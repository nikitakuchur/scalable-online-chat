package com.github.nikitakuchur.chatgateway.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouteConfiguration {

    private final ClientAddressResolver clientAddressResolver;

    @Value("${chat-gateway.user-service.url}")
    private String userServiceUrl;
    @Value("${chat-gateway.chat-manager.url}")
    private String chatManagerUrl;
    @Value("${chat-gateway.chat-service.url}")
    private String chatServiceUrl;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r
                        .path("/api/signup", "/api/login", "/api/logout", "/api/refresh-token")
                        .filters(f -> f.requestRateLimiter(c -> c.setKeyResolver(clientAddressResolver)))
                        .uri(userServiceUrl))
                .route(r -> r
                        .path("/api/chats/**")
                        .filters(f -> f.requestRateLimiter(c -> c.setKeyResolver(clientAddressResolver)))
                        .uri(chatManagerUrl))
                .route(r -> r
                        .path("/ws")
                        .filters(f -> f.requestRateLimiter(c -> c.setKeyResolver(clientAddressResolver)))
                        .uri(chatServiceUrl))
                .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        // 10 requests per second
        return new RedisRateLimiter(10, 10, 1);
    }
}
