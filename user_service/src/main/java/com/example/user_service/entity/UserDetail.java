package com.example.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_detail")
@Data
public class UserDetail {

    @Id
    private Long userId;

    private String gender;
    private String address;
    private String phone;
}