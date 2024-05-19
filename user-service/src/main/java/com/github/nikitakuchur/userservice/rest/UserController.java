package com.github.nikitakuchur.userservice.rest;

import com.github.nikitakuchur.userservice.jwt.JwtUser;
import com.github.nikitakuchur.userservice.jwt.TokenPair;
import com.github.nikitakuchur.userservice.model.dto.LoginRequest;
import com.github.nikitakuchur.userservice.model.dto.RefreshTokenRequest;
import com.github.nikitakuchur.userservice.model.dto.SignupRequest;
import com.github.nikitakuchur.userservice.services.AuthService;
import com.github.nikitakuchur.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/signup")
    public void signup(@RequestBody @Validated SignupRequest signupRequest) {
        userService.create(signupRequest);
    }

    @PostMapping("/login")
    public TokenPair login(@RequestBody @Validated LoginRequest loginRequest) {
        return authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
    }

    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal JwtUser jwtUser) {
        authService.logout(jwtUser.sessionId());
    }

    @PostMapping("/refresh-token")
    public TokenPair refreshToken(@RequestBody @Validated RefreshTokenRequest request) {
        return authService.refreshToken(request.getRefreshToken());
    }

    @GetMapping("/sessions")
    public List<String> getSessions(@AuthenticationPrincipal JwtUser jwtUser) {
        return authService.getSessions(jwtUser.username());
    }

    @DeleteMapping("/sessions")
    public ResponseEntity<String> killAllSessions(@AuthenticationPrincipal JwtUser jwtUse) {
        authService.killAllSessions(jwtUse.username());
        return ResponseEntity.ok("All sessions were killed.");
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<String> killSession(@PathVariable String sessionId) {
        authService.killSession(sessionId);
        return ResponseEntity.ok("The session was killed.");
    }
}
