package com.example.demo.dto.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MainIssueReqDto {
    @NotEmpty
    private String description;
    private boolean reportable;
}
