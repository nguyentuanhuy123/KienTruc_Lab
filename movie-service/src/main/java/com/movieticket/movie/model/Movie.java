package com.movieticket.movie.model;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "movies") @Data
public class Movie {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Integer durationMinutes;
    @Column(nullable = false)
    private BigDecimal price;
    private Integer availableSeats = 100;
    private String genre;
    private LocalDateTime showTime;
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
