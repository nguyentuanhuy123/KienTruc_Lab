package com.movieticket.booking.config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String BOOKING_EXCHANGE = "booking.exchange";
    public static final String BOOKING_CREATED_QUEUE = "booking.created.queue";
    public static final String BOOKING_CREATED_KEY = "booking.created";
    public static final String BOOKING_STATUS_QUEUE = "booking.status.queue";
    public static final String BOOKING_STATUS_KEY = "booking.status.#";

    @Bean public TopicExchange bookingExchange() { return new TopicExchange(BOOKING_EXCHANGE); }
    @Bean public Queue bookingCreatedQueue() { return QueueBuilder.durable(BOOKING_CREATED_QUEUE).build(); }
    @Bean public Queue bookingStatusQueue() { return QueueBuilder.durable(BOOKING_STATUS_QUEUE).build(); }
    @Bean public Binding bookingCreatedBinding() {
        return BindingBuilder.bind(bookingCreatedQueue()).to(bookingExchange()).with(BOOKING_CREATED_KEY);
    }
    @Bean public Binding bookingStatusBinding() {
        return BindingBuilder.bind(bookingStatusQueue()).to(bookingExchange()).with(BOOKING_STATUS_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 1. Đăng ký module để hiểu LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
        // 2. Chuyển ngày tháng thành chuỗi ISO-8601 thay vì mảng số
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new Jackson2JsonMessageConverter(objectMapper);
    }
    @Bean public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(messageConverter());
        return t;
    }
}
