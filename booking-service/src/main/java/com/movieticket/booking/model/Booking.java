package com.movieticket.booking.model;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "bookings") @Data
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Long movieId;
    private String movieTitle;
    private String username;
    @Column(nullable = false)
    private Integer seats;
    @Column(nullable = false)
    private BigDecimal totalPrice;
    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, PAID, FAILED
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
