package com.cinemaabyss.events.service;

import com.cinemaabyss.events.config.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventConsumerService {

    @KafkaListener(
            topics = KafkaConfig.MOVIE_EVENTS_TOPIC,
            groupId = "cinemaabyss-events-group"
    )
    public void consumeMovieEvent(String message) {
        log.info("========================================");
        log.info("Получено событие фильма из Kafka:");
        log.info("Топик: {}", KafkaConfig.MOVIE_EVENTS_TOPIC);
        log.info("Сообщение: {}", message);
        log.info("========================================");
    }

    @KafkaListener(
            topics = KafkaConfig.USER_EVENTS_TOPIC,
            groupId = "cinemaabyss-events-group"
    )
    public void consumeUserEvent(String message) {
        log.info("========================================");
        log.info("Получено событие пользователя из Kafka:");
        log.info("Топик: {}", KafkaConfig.USER_EVENTS_TOPIC);
        log.info("Сообщение: {}", message);
        log.info("========================================");
    }

    @KafkaListener(
            topics = KafkaConfig.PAYMENT_EVENTS_TOPIC,
            groupId = "cinemaabyss-events-group"
    )
    public void consumePaymentEvent(String message) {
        log.info("========================================");
        log.info("Получено событие платежа из Kafka:");
        log.info("Топик: {}", KafkaConfig.PAYMENT_EVENTS_TOPIC);
        log.info("Сообщение: {}", message);
        log.info("========================================");
    }
}
