package com.example.demo.services;

import com.example.demo.dto.external.ExternalDataDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ExternalServiceClient.
 * Demonstrates how to test external service integration with mocking.
 */
@ExtendWith(MockitoExtension.class)
class ExternalServiceClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private ExternalServiceClient externalServiceClient;

    @Test
    void fetchExternalData_Success() {
        ExternalDataDto expectedData = new ExternalDataDto();
        expectedData.setId(1L);
        expectedData.setUserId(1L);
        expectedData.setTitle("Test Title");
        expectedData.setCompleted(false);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(eq("X-Trace-Id"), any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ExternalDataDto.class)).thenReturn(Mono.just(expectedData));

        ExternalDataDto result = externalServiceClient.fetchExternalData();

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Title", result.getTitle());
        assertFalse(result.getCompleted());

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri("/todos/1");
        verify(requestHeadersSpec).header(eq("X-Trace-Id"), any());
    }

    @Test
    void fetchExternalData_ServiceUnavailable_ReturnsNull() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(eq("X-Trace-Id"), any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ExternalDataDto.class)).thenReturn(Mono.error(new RuntimeException("Service unavailable")));

        ExternalDataDto result = externalServiceClient.fetchExternalData();

        assertNull(result, "Should return null when external service is unavailable");
    }

    @Test
    void fetchExternalData_Timeout_ReturnsNull() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(eq("X-Trace-Id"), any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ExternalDataDto.class))
                .thenReturn(Mono.delay(Duration.ofSeconds(10)).map(i -> new ExternalDataDto()));

        ExternalDataDto result = externalServiceClient.fetchExternalData();

        assertNull(result, "Should return null on timeout");
    }
}

