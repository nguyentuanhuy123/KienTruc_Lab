package com.spacebased.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "space.exchange";
    public static final String WRITE_QUEUE = "space.write.queue";
    public static final String READ_QUEUE = "space.read.queue";
    public static final String WRITE_ROUTING_KEY = "space.write";
    public static final String READ_ROUTING_KEY = "space.read";
    public static final String AUDIT_QUEUE = "space.audit.queue";

    @Bean
    public TopicExchange spaceExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue writeQueue() {
        return QueueBuilder.durable(WRITE_QUEUE).build();
    }

    @Bean
    public Queue readQueue() {
        return QueueBuilder.durable(READ_QUEUE).build();
    }

    @Bean
    public Binding writeBinding(Queue writeQueue, TopicExchange spaceExchange) {
        return BindingBuilder.bind(writeQueue).to(spaceExchange).with(WRITE_ROUTING_KEY);
    }

    @Bean
    public Binding readBinding(Queue readQueue, TopicExchange spaceExchange) {
        return BindingBuilder.bind(readQueue).to(spaceExchange).with(READ_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public Queue auditQueue() {
        return QueueBuilder.durable(AUDIT_QUEUE).build();
    }
    @Bean
    public Binding auditBinding(
            Queue auditQueue,
            TopicExchange exchange) {

        return BindingBuilder
                .bind(auditQueue)
                .to(exchange)
                .with("#");
    }
}