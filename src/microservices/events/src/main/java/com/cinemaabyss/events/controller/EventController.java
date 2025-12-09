package com.cinemaabyss.events.controller;

import com.cinemaabyss.events.model.EventResponse;
import com.cinemaabyss.events.model.MovieEvent;
import com.cinemaabyss.events.model.PaymentEvent;
import com.cinemaabyss.events.model.UserEvent;
import com.cinemaabyss.events.service.EventProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventProducerService producerService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Boolean>> health() {
        return ResponseEntity.ok(Map.of("status", true));
    }

    @PostMapping("/movie")
    public ResponseEntity<EventResponse> createMovieEvent(@RequestBody MovieEvent event) {
        log.info("Получен запрос на создание события фильма: {}", event);

        try {
            EventResponse response = producerService.sendMovieEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Ошибка при создании события фильма", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(EventResponse.builder()
                            .status("error")
                            .build());
        }
    }

    @PostMapping("/user")
    public ResponseEntity<EventResponse> createUserEvent(@RequestBody UserEvent event) {
        log.info("Получен запрос на создание события пользователя: {}", event);

        try {
            EventResponse response = producerService.sendUserEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Ошибка при создании события пользователя", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(EventResponse.builder()
                            .status("error")
                            .build());
        }
    }

    @PostMapping("/payment")
    public ResponseEntity<EventResponse> createPaymentEvent(@RequestBody PaymentEvent event) {
        log.info("Получен запрос на создание события платежа: {}", event);

        try {
            EventResponse response = producerService.sendPaymentEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Ошибка при создании события платежа", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(EventResponse.builder()
                            .status("error")
                            .build());
        }
    }
}
