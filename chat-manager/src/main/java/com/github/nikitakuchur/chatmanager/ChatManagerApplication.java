package com.github.nikitakuchur.chatmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class ChatManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatManagerApplication.class, args);
    }

}
