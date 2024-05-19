package com.github.nikitakuchur.userservice.services;

import com.github.nikitakuchur.userservice.exceptions.UserAlreadyExistsException;
import com.github.nikitakuchur.userservice.model.Role;
import com.github.nikitakuchur.userservice.model.User;
import com.github.nikitakuchur.userservice.model.dto.SignupRequest;
import com.github.nikitakuchur.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * A service for managing users.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new account.
     *
     * @param signupRequest the required information to create a new account
     * @throws UserAlreadyExistsException if a user with the same email or username already exists
     */
    public void create(SignupRequest signupRequest) {
        try {
            User user = User.builder()
                    .email(signupRequest.getEmail())
                    .username(signupRequest.getUsername())
                    .password(passwordEncoder.encode(signupRequest.getPassword()))
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
        } catch (DuplicateKeyException e) {
            throw new UserAlreadyExistsException("A user with the same email or username already exists.");
        }
    }

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the found user, or empty if no user was found
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " has not been found"));
    }
}
