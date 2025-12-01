package com.cinemaabyss.proxy.service;

import com.cinemaabyss.proxy.config.ProxyConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyService {

    private final ProxyConfig proxyConfig;
    private final WebClient.Builder webClientBuilder;
    private final Random random = new Random();

    public ResponseEntity<String> routeMoviesRequest(String path, HttpMethod method,
                                                      String body, HttpHeaders headers) {
        String targetUrl = determineMoviesTarget();
        log.info("Маршрутизация запроса movies в: {}", targetUrl);

        return forwardRequest(targetUrl + path, method, body, headers);
    }

    public ResponseEntity<String> routeToMonolith(String path, HttpMethod method,
                                                    String body, HttpHeaders headers) {
        String targetUrl = proxyConfig.getMonolithUrl() + path;
        log.info("Маршрутизация запроса в монолит: {}", targetUrl);

        return forwardRequest(targetUrl, method, body, headers);
    }

    public ResponseEntity<String> routeToEvents(String path, HttpMethod method,
                                                 String body, HttpHeaders headers) {
        String targetUrl = proxyConfig.getEventsServiceUrl() + path;
        log.info("Маршрутизация запроса в events service: {}", targetUrl);

        return forwardRequest(targetUrl, method, body, headers);
    }

    private String determineMoviesTarget() {
        if (!proxyConfig.isGradualMigration()) {
            // Если постепенная миграция отключена, всегда направляем в movies service
            log.debug("Постепенная миграция отключена, направляем в movies service");
            return proxyConfig.getMoviesServiceUrl();
        }

        // Реализуем процентную маршрутизацию
        int randomValue = random.nextInt(100);
        if (randomValue < proxyConfig.getMoviesMigrationPercent()) {
            log.debug("Направляем в movies service (шанс {}%, выпало {})",
                     proxyConfig.getMoviesMigrationPercent(), randomValue);
            return proxyConfig.getMoviesServiceUrl();
        } else {
            log.debug("Направляем в монолит (шанс {}%, выпало {})",
                     100 - proxyConfig.getMoviesMigrationPercent(), randomValue);
            return proxyConfig.getMonolithUrl();
        }
    }

    private ResponseEntity<String> forwardRequest(String targetUrl, HttpMethod method,
                                                    String body, HttpHeaders headers) {
        try {
            WebClient webClient = webClientBuilder.build();

            WebClient.RequestBodySpec request = webClient
                    .method(method)
                    .uri(targetUrl)
                    .headers(h -> {
                        // Копируем заголовки, но фильтруем проблемные
                        headers.forEach((key, value) -> {
                            if (!key.equalsIgnoreCase("host") &&
                                !key.equalsIgnoreCase("content-length")) {
                                h.addAll(key, value);
                            }
                        });
                    });

            // Добавляем тело запроса, если оно присутствует
            if (body != null && !body.isEmpty() &&
                (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH)) {
                request.bodyValue(body);
            }

            // Выполняем запрос и получаем ответ
            ResponseEntity<String> response = request
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            return response != null ? response : ResponseEntity.internalServerError().build();

        } catch (Exception e) {
            log.error("Ошибка при пересылке запроса в {}: {}", targetUrl, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
