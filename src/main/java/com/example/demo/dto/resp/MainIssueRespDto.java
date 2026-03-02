package com.example.demo.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Schema(description = "Response DTO for Main Issue")
public class MainIssueRespDto {
    @Schema(description = "Unique identifier of the main issue", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Version number for optimistic locking", example = "0")
    private long version;

    @Schema(description = "Timestamp when the main issue was created", example = "2026-03-02T10:30:00+01:00")
    private ZonedDateTime created;

    @Schema(description = "Description of the main issue", example = "Critical bug in payment processing")
    private String description;

    @Schema(description = "Whether the issue is reportable", example = "true")
    private boolean reportable;
}
