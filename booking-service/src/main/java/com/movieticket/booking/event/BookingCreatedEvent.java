package com.movieticket.booking.event;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor
public class BookingCreatedEvent {
    private Long bookingId;
    private Long userId;
    private String username;
    private Long movieId;
    private String movieTitle;
    private Integer seats;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt = LocalDateTime.now();

    public BookingCreatedEvent(Long bookingId, Long userId, String username,
                                Long movieId, String movieTitle,
                                Integer seats, BigDecimal totalPrice) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.username = username;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.seats = seats;
        this.totalPrice = totalPrice;
    }
}
