package com.spacebased.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho CLIENT-request gửi vào hệ thống
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequestDTO {
    private String key;
    private String value;
    private String operation; // "read" hoặc "write"
}
