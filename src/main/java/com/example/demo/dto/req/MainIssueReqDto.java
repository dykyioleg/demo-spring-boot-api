package com.example.demo.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request DTO for creating or updating a Main Issue")
public class MainIssueReqDto {
    @NotEmpty
    @Schema(description = "Description of the main issue", example = "Critical bug in payment processing")
    private String description;

    @Schema(description = "Whether the issue is reportable", example = "true", defaultValue = "false")
    private boolean reportable;
}
