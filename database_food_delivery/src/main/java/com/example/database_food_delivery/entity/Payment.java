package com.example.database_food_delivery.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Payment {
    private Integer id;
    private Integer orderId;
    private LocalDateTime paymentDate;
    private BigDecimal amount;
    private String method;

    public Payment() {
    }

    public Payment(Integer id, Integer orderId, LocalDateTime paymentDate, BigDecimal amount, String method) {
        this.id = id;
        this.orderId = orderId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.method = method;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}