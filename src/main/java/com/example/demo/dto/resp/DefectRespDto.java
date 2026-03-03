package com.example.demo.dto.resp;

import com.example.demo.dto.external.ExternalDataDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Schema(description = "Response DTO for Defect")
public class DefectRespDto {
    @Schema(description = "Unique identifier of the defect", example = "987e6543-e21b-45d3-a654-426614174999")
    private UUID id;

    @Schema(description = "Version number for optimistic locking", example = "0")
    private long version;

    @Schema(description = "Timestamp when the defect was created", example = "2026-03-02T10:30:00+01:00")
    private ZonedDateTime created;

    @Schema(description = "ID of the main issue this defect is associated with", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID mainIssueId;

    @Schema(description = "Additional data from external service (null if service is unavailable or returns no data)")
    private ExternalDataDto externalData;
}

