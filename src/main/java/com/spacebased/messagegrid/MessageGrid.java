package com.spacebased.messagegrid;

import com.spacebased.config.RabbitConfig;
import com.spacebased.model.DataEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageGrid {

    private final RabbitTemplate rabbitTemplate;

    public void publishToWriteQueue(DataEntry entry) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.WRITE_ROUTING_KEY,
                entry
        );
        log.debug("[RabbitMQ] published write message → key={}", entry.getKey());
    }

    public void publishToReadQueue(String key) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.READ_ROUTING_KEY,
                key
        );
        log.debug("[RabbitMQ] published read message → key={}", key);
    }
}