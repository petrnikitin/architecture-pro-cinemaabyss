package com.cinemaabyss.events.service;

import com.cinemaabyss.events.config.KafkaConfig;
import com.cinemaabyss.events.model.EventResponse;
import com.cinemaabyss.events.model.MovieEvent;
import com.cinemaabyss.events.model.PaymentEvent;
import com.cinemaabyss.events.model.UserEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public EventResponse sendMovieEvent(MovieEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            log.info("Отправка события фильма в Kafka: {}", message);

            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(KafkaConfig.MOVIE_EVENTS_TOPIC, message);

            SendResult<String, String> result = future.get();

            log.info("Событие фильма успешно отправлено в партицию {} с offset {}",
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());

            return EventResponse.builder()
                    .status("success")
                    .partition(result.getRecordMetadata().partition())
                    .offset(result.getRecordMetadata().offset())
                    .event(event)
                    .build();

        } catch (Exception e) {
            log.error("Ошибка при отправке события фильма: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить событие фильма", e);
        }
    }

    public EventResponse sendUserEvent(UserEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            log.info("Отправка события пользователя в Kafka: {}", message);

            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(KafkaConfig.USER_EVENTS_TOPIC, message);

            SendResult<String, String> result = future.get();

            log.info("Событие пользователя успешно отправлено в партицию {} с offset {}",
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());

            return EventResponse.builder()
                    .status("success")
                    .partition(result.getRecordMetadata().partition())
                    .offset(result.getRecordMetadata().offset())
                    .event(event)
                    .build();

        } catch (Exception e) {
            log.error("Ошибка при отправке события пользователя: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить событие пользователя", e);
        }
    }

    public EventResponse sendPaymentEvent(PaymentEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            log.info("Отправка события платежа в Kafka: {}", message);

            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(KafkaConfig.PAYMENT_EVENTS_TOPIC, message);

            SendResult<String, String> result = future.get();

            log.info("Событие платежа успешно отправлено в партицию {} с offset {}",
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());

            return EventResponse.builder()
                    .status("success")
                    .partition(result.getRecordMetadata().partition())
                    .offset(result.getRecordMetadata().offset())
                    .event(event)
                    .build();

        } catch (Exception e) {
            log.error("Ошибка при отправке события платежа: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить событие платежа", e);
        }
    }
}
