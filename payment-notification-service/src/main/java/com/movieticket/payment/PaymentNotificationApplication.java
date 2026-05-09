package com.movieticket.payment;

import org.springframework.amqp.core.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PaymentNotificationApplication {
    public static void main(String[] args) { SpringApplication.run(PaymentNotificationApplication.class, args); }

    @Bean
    public Queue bookingFailedQueue() {
        return QueueBuilder.durable("booking.failed.queue").build();
    }

    @Bean
    public Binding bookingFailedBinding(TopicExchange bookingExchange, Queue bookingFailedQueue) {
        return BindingBuilder.bind(bookingFailedQueue)
                .to(bookingExchange)
                .with("booking.status.failed");
    }
}
