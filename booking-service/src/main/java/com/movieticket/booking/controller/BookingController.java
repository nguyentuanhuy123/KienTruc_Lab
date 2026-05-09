package com.movieticket.booking.controller;
import com.movieticket.booking.model.Booking;
import com.movieticket.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController @RequestMapping("/api/bookings")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> req) {
        try {
            Booking booking = bookingService.createBooking(
                    Long.valueOf(req.get("userId").toString()),
                    req.get("username").toString(),
                    Long.valueOf(req.get("movieId").toString()),
                    req.get("movieTitle").toString(),
                    Integer.valueOf(req.get("seats").toString()),
                    new BigDecimal(req.get("pricePerSeat").toString())
            );
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getUserBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("service", "booking-service", "status", "UP"));
    }
}
