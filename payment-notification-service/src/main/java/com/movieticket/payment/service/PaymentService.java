package com.movieticket.payment.service;

import com.movieticket.payment.config.RabbitMQConfig;
import com.movieticket.payment.event.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Random;

@Service @RequiredArgsConstructor @Slf4j
public class PaymentService {

    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();

    // ── Consume BOOKING_CREATED ───────────────────────────────
    @RabbitListener(queues = RabbitMQConfig.BOOKING_CREATED_QUEUE)
    public void processPayment(BookingCreatedEvent event) {
        log.info("[EVENT CONSUMED] BOOKING_CREATED → bookingId={} user='{}' movie='{}' total={}",
                event.getBookingId(), event.getUsername(), event.getMovieTitle(), event.getTotalPrice());

        // Simulate processing time
        try { Thread.sleep(1000 + random.nextInt(2000)); } catch (InterruptedException ignored) {}

        // Random success 70% / fail 30%
        boolean success = random.nextDouble() < 0.7;

        if (success) {
            log.info("[PAYMENT SUCCESS] bookingId={} amount={}", event.getBookingId(), event.getTotalPrice());
            publishPaymentCompleted(event);
        } else {
            log.warn("[PAYMENT FAILED] bookingId={} — insufficient funds (simulated)", event.getBookingId());
            publishBookingFailed(event);
        }
    }

    private void publishPaymentCompleted(BookingCreatedEvent event) {
        Map<String, Object> payload = Map.of(
                "bookingId", event.getBookingId(),
                "userId", event.getUsername(),
                "movieTitle", event.getMovieTitle(),
                "amount", event.getTotalPrice(),
                "status", "PAID",
                "eventType", "PAYMENT_COMPLETED"
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BOOKING_EXCHANGE,
                RabbitMQConfig.BOOKING_STATUS_KEY_PAID,
                payload);
        log.info("[EVENT PUBLISHED] PAYMENT_COMPLETED → bookingId={}", event.getBookingId());
    }

    private void publishBookingFailed(BookingCreatedEvent event) {
        Map<String, Object> payload = Map.of(
                "bookingId", event.getBookingId(),
                "userId", event.getUsername(),
                "movieTitle", event.getMovieTitle(),
                "status", "FAILED",
                "eventType", "BOOKING_FAILED"
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BOOKING_EXCHANGE,
                RabbitMQConfig.BOOKING_STATUS_KEY_FAILED,
                payload);
        log.warn("[EVENT PUBLISHED] BOOKING_FAILED → bookingId={}", event.getBookingId());
    }
}
