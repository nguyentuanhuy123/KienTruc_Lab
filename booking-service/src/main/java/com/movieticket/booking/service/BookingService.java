package com.movieticket.booking.service;

import com.movieticket.booking.config.RabbitMQConfig;
import com.movieticket.booking.event.BookingCreatedEvent;
import com.movieticket.booking.model.Booking;
import com.movieticket.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service @RequiredArgsConstructor @Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RabbitTemplate rabbitTemplate;

    public Booking createBooking(Long userId, String username,
                                  Long movieId, String movieTitle,
                                  Integer seats, BigDecimal pricePerSeat) {
        BigDecimal total = pricePerSeat.multiply(BigDecimal.valueOf(seats));

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setUsername(username);
        booking.setMovieId(movieId);
        booking.setMovieTitle(movieTitle);
        booking.setSeats(seats);
        booking.setTotalPrice(total);
        booking.setStatus("PENDING");
        bookingRepository.save(booking);

        // ── Publish BOOKING_CREATED (KHÔNG gọi Payment trực tiếp) ──
        BookingCreatedEvent event = new BookingCreatedEvent(
                booking.getId(), userId, username,
                movieId, movieTitle, seats, total);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BOOKING_EXCHANGE,
                RabbitMQConfig.BOOKING_CREATED_KEY,
                event);
        log.info("[EVENT PUBLISHED] BOOKING_CREATED → bookingId={} userId={} movie='{}' seats={} total={}",
                booking.getId(), userId, movieTitle, seats, total);

        return booking;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // ── Listen for payment result to update booking status ──
    @RabbitListener(queues = RabbitMQConfig.BOOKING_STATUS_QUEUE)
    public void handlePaymentResult(Map<String, Object> payload) {
        Long bookingId = Long.valueOf(payload.get("bookingId").toString());
        String status = payload.get("status").toString();

        bookingRepository.findById(bookingId).ifPresent(booking -> {
            booking.setStatus(status);
            booking.setUpdatedAt(LocalDateTime.now());
            bookingRepository.save(booking);
            log.info("[BOOKING STATUS UPDATED] bookingId={} → status={}", bookingId, status);
        });
    }
}
