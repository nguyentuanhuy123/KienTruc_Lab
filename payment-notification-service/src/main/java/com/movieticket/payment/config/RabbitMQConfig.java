package com.movieticket.payment.config;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // Consume from
    public static final String BOOKING_EXCHANGE = "booking.exchange";
    public static final String BOOKING_CREATED_QUEUE = "booking.created.queue";

    // Publish to (feedback)
    public static final String BOOKING_STATUS_KEY_PAID = "booking.status.paid";
    public static final String BOOKING_STATUS_KEY_FAILED = "booking.status.failed";

    // Notification queue
    public static final String NOTIFICATION_QUEUE = "notification.queue";

    @Bean public Jackson2JsonMessageConverter messageConverter() { return new Jackson2JsonMessageConverter(); }
    @Bean public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(messageConverter());
        return t;
    }
    @Bean
    public Queue notificationQueue() { return QueueBuilder.durable(NOTIFICATION_QUEUE).build(); }
    @Bean
    public TopicExchange bookingExchange() { return new TopicExchange(BOOKING_EXCHANGE); }
    @Bean
    public Queue bookingCreatedQueue() { return QueueBuilder.durable(BOOKING_CREATED_QUEUE).build(); }
    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(bookingExchange())
                .with("booking.status.paid");
    }
}
// append - handled inline
