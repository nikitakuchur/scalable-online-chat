package com.github.nikitakuchur.userservice.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nikitakuchur.userservice.IntegrationTest;
import com.github.nikitakuchur.userservice.model.RefreshToken;
import com.github.nikitakuchur.userservice.model.Role;
import com.github.nikitakuchur.userservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void initDatabase() {
        mongoTemplate.findAllAndRemove(Query.query(new Criteria()), User.class);
        mongoTemplate.findAllAndRemove(Query.query(new Criteria()), RefreshToken.class);

        mongoTemplate.save(User.builder()
                .email("user@gmail.com")
                .username("user")
                .password("$2a$10$h5EXgEphizfFsPSfLLjyDO2Yg3ayNWrnIJ2PCGeSSCOZt7PHUqiG6") // 12345678
                .role(Role.USER)
                .build());
    }

    @Test
    void signupTest() throws Exception {
        mockMvc.perform(post("/api/signup")
                        .contentType("application/json")
                        .content(toJson(Map.of(
                                "username", "alice",
                                "password", "87654321",
                                "email", "alice@email.com"
                        ))))
                .andExpect(status().isOk());
    }

    @Test
    void signupExistingUserTest() throws Exception {
        mockMvc.perform(post("/api/signup")
                        .contentType("application/json")
                        .content(toJson(Map.of(
                                "username", "user",
                                "password", "12345678",
                                "email", "user@gmail.com"
                        ))))
                .andExpect(status().isConflict());
    }

    @Test
    void loginTest() throws Exception {
        var result = mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(toJson(Map.of(
                                "username", "user",
                                "password", "12345678"
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        var tokens = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertNotNull(tokens.get("accessToken"));
        assertNotNull(tokens.get("refreshToken"));
    }

    @Test
    void invalidLoginTest() throws Exception {
        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(toJson(Map.of(
                                "username", "user",
                                "password", "87654321"
                        ))))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    void refreshTokenTest() throws Exception {
        var loginResult = mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(toJson(Map.of(
                                "username", "user",
                                "password", "12345678"
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        var tokens = toMap(loginResult.getResponse().getContentAsString());

        var refreshTokenResult = mockMvc.perform(post("/api/refresh-token")
                        .contentType("application/json")
                        .content(toJson(Map.of(
                                "refreshToken", tokens.get("refreshToken")
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        var refreshedTokens = toMap(refreshTokenResult.getResponse().getContentAsString());
        assertNotNull(refreshedTokens.get("accessToken"));
        assertNotNull(refreshedTokens.get("refreshToken"));
    }

    @Test
    void usedRefreshTokenTest() throws Exception {
        var loginResult = mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(toJson(Map.of(
                                "username", "user",
                                "password", "12345678"
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        var tokens = toMap(loginResult.getResponse().getContentAsString());

        mockMvc.perform(post("/api/refresh-token")
                        .contentType("application/json")
                        .content(toJson(Map.of(
                                "refreshToken", tokens.get("refreshToken")
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(post("/api/refresh-token")
                        .contentType("application/json")
                        .content(toJson(Map.of(
                                "refreshToken", tokens.get("refreshToken")
                        ))))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    void logoutTest() throws Exception {
        var loginResult = mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(toJson(Map.of(
                                "username", "user",
                                "password", "12345678"
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        var tokens = toMap(loginResult.getResponse().getContentAsString());

        mockMvc.perform(post("/api/logout")
                        .header("Authorization", "Bearer " + tokens.get("accessToken")))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(post("/api/refresh-token")
                        .contentType("application/json")
                        .content(toJson(Map.of(
                                "refreshToken", tokens.get("refreshToken")
                        ))))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    void twoSessionsAndLogoutTest() throws Exception {
        var loginRequest = toJson(Map.of(
                "username", "user",
                "password", "12345678"
        ));

        var loginResult1 = mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        var tokens1 = toMap(loginResult1.getResponse().getContentAsString());

        var loginResult2 = mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        var tokens2 = toMap(loginResult2.getResponse().getContentAsString());

        mockMvc.perform(post("/api/logout")
                        .header("Authorization", "Bearer " + tokens1.get("accessToken")))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(post("/api/refresh-token")
                        .contentType("application/json")
                        .content(toJson(Map.of(
                                "refreshToken", tokens1.get("refreshToken")
                        ))))
                .andExpect(status().isUnauthorized())
                .andReturn();

        mockMvc.perform(post("/api/refresh-token")
                        .contentType("application/json")
                        .content(toJson(Map.of(
                                "refreshToken", tokens2.get("refreshToken")
                        ))))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void twoSessionsAndReusedTokenTest() throws Exception {
        var loginRequest = toJson(Map.of(
                "username", "user",
                "password", "12345678"
        ));

        var loginResult1 = mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        var tokens1 = toMap(loginResult1.getResponse().getContentAsString());

        var loginResult2 = mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        var tokens2 = toMap(loginResult2.getResponse().getContentAsString());

        mockMvc.perform(post("/api/refresh-token")
                        .contentType("application/json")
                        .content(toJson(Map.of(
                                "refreshToken", tokens1.get("refreshToken")
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(post("/api/refresh-token")
                        .contentType("application/json")
                        .content(toJson(Map.of(
                                "refreshToken", tokens1.get("refreshToken")
                        ))))
                .andExpect(status().isUnauthorized())
                .andReturn();

        mockMvc.perform(post("/api/refresh-token")
                        .contentType("application/json")
                        .content(toJson(Map.of(
                                "refreshToken", tokens2.get("refreshToken")
                        ))))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getSessionsTest() throws Exception {
        var loginRequest = toJson(Map.of(
                "username", "user",
                "password", "12345678"
        ));

        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        var loginResult = mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        var tokens = toMap(loginResult.getResponse().getContentAsString());

        var result = mockMvc.perform(get("/api/sessions")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + tokens.get("accessToken")))
                .andExpect(status().isOk())
                .andReturn();

        var sessions = toList(result.getResponse().getContentAsString());

        assertEquals(2, sessions.size());
    }

    @Test
    void killSessionTest() throws Exception {
        var loginRequest = toJson(Map.of(
                "username", "user",
                "password", "12345678"
        ));

        var loginResult = mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        var tokens = toMap(loginResult.getResponse().getContentAsString());

        var result = mockMvc.perform(get("/api/sessions")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + tokens.get("accessToken")))
                .andExpect(status().isOk())
                .andReturn();

        var sessions = toList(result.getResponse().getContentAsString());
        assertEquals(1, sessions.size());

        mockMvc.perform(delete("/api/sessions/" + sessions.get(0))
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + tokens.get("accessToken")))
                .andExpect(status().isOk())
                .andReturn();

        result = mockMvc.perform(get("/api/sessions")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + tokens.get("accessToken")))
                .andExpect(status().isOk())
                .andReturn();
        sessions = toList(result.getResponse().getContentAsString());

        assertTrue(sessions.isEmpty());
    }

    @Test
    void killAllSessionsTest() throws Exception {
        var loginRequest = toJson(Map.of(
                "username", "user",
                "password", "12345678"
        ));

        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        var loginResult = mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        var tokens = toMap(loginResult.getResponse().getContentAsString());

        var result = mockMvc.perform(get("/api/sessions")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + tokens.get("accessToken")))
                .andExpect(status().isOk())
                .andReturn();

        var sessions = toList(result.getResponse().getContentAsString());
        assertEquals(2, sessions.size());

        mockMvc.perform(delete("/api/sessions")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + tokens.get("accessToken")))
                .andExpect(status().isOk())
                .andReturn();

        result = mockMvc.perform(get("/api/sessions")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + tokens.get("accessToken")))
                .andExpect(status().isOk())
                .andReturn();
        sessions = toList(result.getResponse().getContentAsString());

        assertTrue(sessions.isEmpty());
    }

    @Test
    void useKilledSessionTest() throws Exception {
        var loginRequest = toJson(Map.of(
                "username", "user",
                "password", "12345678"
        ));

        var loginResult = mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        var tokens = toMap(loginResult.getResponse().getContentAsString());

        mockMvc.perform(delete("/api/sessions")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + tokens.get("accessToken")))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(post("/api/refresh-token")
                        .contentType("application/json")
                        .content(toJson(Map.of(
                                "refreshToken", tokens.get("refreshToken")
                        ))))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    private String toJson(Map<?, ?> map) throws JsonProcessingException {
        return objectMapper.writeValueAsString(map);
    }

    private Map<?, ?> toMap(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, Map.class);
    }

    private List<?> toList(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, List.class);
    }
}
