package com.movieticket.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service @Slf4j
public class NotificationService {

    // ── Consume PAYMENT_COMPLETED (notification.queue) ────────
    @RabbitListener(queues = "notification.queue")
    public void sendSuccessNotification(Map<String, Object> payload) {
        Long bookingId = Long.valueOf(payload.get("bookingId").toString());
        String username = payload.get("userId").toString();
        String movieTitle = payload.get("movieTitle").toString();
        Object amount = payload.get("amount");

        // Output thông báo
        log.info("╔══════════════════════════════════════════════════╗");
        log.info("║  🎬  THÔNG BÁO ĐẶT VÉ THÀNH CÔNG                 ║");
        log.info("║  Booking #{}: {} đã đặt vé '{}' thành công!  ║", bookingId, username, movieTitle);
        log.info("║  Số tiền: {} VND                               ║", amount);
        log.info("╚══════════════════════════════════════════════════╝");
        System.out.printf("%n[NOTIFICATION] User %s đã đặt đơn #%d thành công! Phim: %s%n%n",
                username, bookingId, movieTitle);
    }

    // ── Consume BOOKING_FAILED ─────────────────────────────────
    @RabbitListener(queues = "#{@bookingFailedQueue}")
    public void sendFailureNotification(Map<String, Object> payload) {
        Long bookingId = Long.valueOf(payload.get("bookingId").toString());
        String username = payload.get("userId").toString();
        log.warn("[NOTIFICATION] ❌ Đặt vé thất bại! bookingId={} user={}", bookingId, username);
        System.out.printf("%n[NOTIFICATION] Rất tiếc! User %s đặt đơn #%d THẤT BẠI. Vui lòng thử lại.%n%n",
                username, bookingId);
    }
}
