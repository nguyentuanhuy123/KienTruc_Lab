package com.example.user_service.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Integer age;

    private String gender;
    private String address;
    private String phone;
}