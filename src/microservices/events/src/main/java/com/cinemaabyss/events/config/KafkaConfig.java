package com.cinemaabyss.events.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String MOVIE_EVENTS_TOPIC = "movie-events";
    public static final String USER_EVENTS_TOPIC = "user-events";
    public static final String PAYMENT_EVENTS_TOPIC = "payment-events";

    @Bean
    public NewTopic movieEventsTopic() {
        return TopicBuilder.name(MOVIE_EVENTS_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder.name(USER_EVENTS_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentEventsTopic() {
        return TopicBuilder.name(PAYMENT_EVENTS_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
