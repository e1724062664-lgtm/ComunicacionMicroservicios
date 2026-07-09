package com.uti.svcreservations.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;


@Configuration
public class AppConfig {

    @Value("${rooms.service.url}")
    private String roomsServiceUrl;

    // Bean Conexion y lectura
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(3))
                .build();
    }

    // Bean de WebClient configurado con la URL base de svc-rooms
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(roomsServiceUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
