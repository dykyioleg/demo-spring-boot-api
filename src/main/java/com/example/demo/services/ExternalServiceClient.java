package com.example.demo.services;

import com.example.demo.dto.external.ExternalDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Service for calling external APIs.
 * <p>
 * This service demonstrates how to:
 * <ul>
 *   <li>Use WebClient to call external REST APIs</li>
 *   <li>Handle errors gracefully (return null instead of failing)</li>
 *   <li>Add timeouts to prevent hanging requests</li>
 * </ul>
 * <p>
 * For demonstration purposes, this service calls JSONPlaceholder API (https://jsonplaceholder.typicode.com)
 * which is a free fake REST API for testing and prototyping.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalServiceClient {

    private final WebClient webClient;

    /**
     * Fetches data from external service.
     * <p>
     * This method demonstrates calling an external API and handling potential failures.
     * If the external service is unavailable or returns an error, this method returns null
     * instead of failing the entire request.
     *
     * @return ExternalDataDto if successful, null if the service is unavailable or returns an error
     */
    public ExternalDataDto fetchExternalData() {
        log.info("Attempting to fetch data from external service");

        try {
            ExternalDataDto result = webClient.get()
                    .uri("/todos/1")
                    .retrieve()
                    .bodyToMono(ExternalDataDto.class)
                    .timeout(Duration.ofSeconds(5))
                    .doOnSuccess(data -> log.info("Successfully fetched external data: {}", data))
                    .doOnError(error -> log.warn("Failed to fetch external data: {}", error.getMessage()))
                    .onErrorResume(error -> {
                        log.error("Error calling external service, returning null", error);
                        return Mono.empty();
                    })
                    .block();

            return result;
        } catch (Exception e) {
            log.error("Unexpected error while calling external service: {}", e.getMessage(), e);
            return null;
        }
    }
}

