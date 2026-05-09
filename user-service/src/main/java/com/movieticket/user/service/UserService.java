package com.movieticket.user.service;

import com.movieticket.user.config.JwtUtil;
import com.movieticket.user.config.RabbitMQConfig;
import com.movieticket.user.event.UserRegisteredEvent;
import com.movieticket.user.model.User;
import com.movieticket.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RabbitTemplate rabbitTemplate;

    public Map<String, Object> register(String username, String email,
                                         String password, String fullName) {
        if (userRepository.existsByUsername(username))
            throw new RuntimeException("Username already exists: " + username);
        if (userRepository.existsByEmail(email))
            throw new RuntimeException("Email already exists: " + email);

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        userRepository.save(user);

        // ── Publish USER_REGISTERED event ──────────────────────
        UserRegisteredEvent event = new UserRegisteredEvent(
                user.getId(), user.getUsername(), user.getEmail(), user.getFullName());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_EXCHANGE,
                RabbitMQConfig.USER_REGISTERED_ROUTING_KEY,
                event);
        log.info("[EVENT PUBLISHED] USER_REGISTERED → userId={} username={}", user.getId(), username);

        return Map.of(
                "message", "User registered successfully",
                "userId", user.getId(),
                "username", user.getUsername()
        );
    }

    public Map<String, String> login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new RuntimeException("Invalid credentials");

        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        log.info("[LOGIN] User {} logged in", username);
        return Map.of(
                "token", token,
                "username", user.getUsername(),
                "userId", String.valueOf(user.getId()),
                "fullName", user.getFullName() != null ? user.getFullName() : ""
        );
    }
}
