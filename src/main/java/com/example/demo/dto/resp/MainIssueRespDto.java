package com.example.demo.dto.resp;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class MainIssueRespDto {
    private UUID id;
    private long version;
    private ZonedDateTime created;
    private String description;
    private boolean reportable;
}
