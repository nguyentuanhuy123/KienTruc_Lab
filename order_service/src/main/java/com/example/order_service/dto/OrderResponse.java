package com.example.order_service.dto;

import lombok.Data;

@Data
public class OrderResponse {
    private Long id;
    private String status;
    private Double totalAmount;

    private Object user;
}