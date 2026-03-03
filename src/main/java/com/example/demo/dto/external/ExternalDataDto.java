package com.example.demo.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO for data received from external service.
 * This demonstrates calling an external API and including its response in our API response.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "External service data - fetched from a third-party API for demonstration purposes")
public class ExternalDataDto {

    @Schema(description = "User ID from external service", example = "1")
    private Long userId;

    @Schema(description = "ID from external service", example = "1")
    private Long id;

    @Schema(description = "Title from external service", example = "Sample Title")
    private String title;

    @Schema(description = "Completed status from external service", example = "false")
    private Boolean completed;
}

