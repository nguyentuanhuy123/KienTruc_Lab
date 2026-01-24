package com.example.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class AuthController {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> user) {
        String username = user.get("username");
        String password = user.get("password");

        Map<String, String> response = new HashMap<>();

        // Giả lập check pass đơn giản
        if ("admin".equals(username) && "123".equals(password)) {
            response.put("accessToken", tokenProvider.createToken(username, "ROLE_ADMIN", false));
            response.put("refreshToken", tokenProvider.createToken(username, "ROLE_ADMIN", true));
        } else {
            response.put("accessToken", tokenProvider.createToken(username, "ROLE_GUEST", false));
        }
        return response;
    }

    @GetMapping("/admin/dashboard")
    public String admin() { return "Chào Admin! Đây là khu vực bí mật."; }

    @GetMapping("/guest/info")
    public String guest() { return "Chào Guest! Đây là thông tin công khai."; }
}