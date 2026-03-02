package com.example.demo.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefectReqDto {
    @NotNull
    private UUID mainIssueId;
}

