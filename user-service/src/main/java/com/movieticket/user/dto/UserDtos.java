package com.movieticket.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;

// ── Request DTOs ──────────────────────────────────────────

public class UserDtos {

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "Username không được trống")
        @Size(min = 3, max = 50)
        private String username;

        @NotBlank @Email(message = "Email không hợp lệ")
        private String email;

        @NotBlank
        @Size(min = 6, message = "Password tối thiểu 6 ký tự")
        private String password;
    }

    @Data
    public static class LoginRequest {
        @NotBlank private String username;
        @NotBlank private String password;
    }

    // ── Response DTOs ─────────────────────────────────────

    @Data @Builder
    public static class AuthResponse {
        private String token;
        private String username;
        private String email;
        private String role;
        private String message;
    }

    @Data @Builder
    public static class UserResponse {
        private String id;
        private String username;
        private String email;
        private String role;
        private String createdAt;
    }
}
