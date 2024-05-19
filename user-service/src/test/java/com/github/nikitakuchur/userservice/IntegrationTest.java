package com.github.nikitakuchur.userservice;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

public abstract class IntegrationTest {

    private static final MongoDBContainer container = new MongoDBContainer("mongo:8.0-rc");

    @BeforeAll
    static void runContainer() {
        container.start();
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", container::getConnectionString);
    }
}
