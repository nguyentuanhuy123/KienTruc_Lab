package com.example.database_food_delivery.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Shipment {
    private Integer id;
    private Integer orderId;
    private LocalDateTime shippingDate;
    private String status;

    public Shipment(Integer id, Integer orderId, LocalDateTime shippingDate, String status) {
        this.id = id;
        this.orderId = orderId;
        this.shippingDate = shippingDate;
        this.status = status;
    }

    public Shipment() {
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

    public LocalDateTime getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(LocalDateTime shippingDate) {
        this.shippingDate = shippingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
