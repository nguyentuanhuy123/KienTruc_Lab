package com.movieticket.payment.event;
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
    private LocalDateTime createdAt;
}
