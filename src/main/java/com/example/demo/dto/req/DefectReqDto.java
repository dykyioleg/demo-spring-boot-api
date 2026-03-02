package com.example.demo.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request DTO for creating or updating a Defect")
public class DefectReqDto {
    @NotNull
    @Schema(description = "ID of the main issue this defect is associated with",
            example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID mainIssueId;
}

