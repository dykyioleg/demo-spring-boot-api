package com.example.demo.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Configuration for WebClient used to call external services.
 * <p>
 * This configuration demonstrates how to set up a WebClient with:
 * <ul>
 *   <li>Connection timeout</li>
 *   <li>Read/Write timeouts</li>
 *   <li>Base URL from application properties</li>
 * </ul>
 */
@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${external.service.base-url:https://jsonplaceholder.typicode.com}")
    private String baseUrl;

    @Value("${external.service.timeout.connection:5000}")
    private int connectionTimeout;

    @Value("${external.service.timeout.read:5000}")
    private int readTimeout;

    @Bean
    public WebClient webClient() {
        log.info("Configuring WebClient with base URL: {}", baseUrl);

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .responseTimeout(Duration.ofMillis(readTimeout))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}

