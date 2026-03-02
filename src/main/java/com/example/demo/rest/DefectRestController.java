package com.example.demo.rest;

import com.example.demo.dto.req.DefectReqDto;
import com.example.demo.dto.resp.DefectRespDto;
import com.example.demo.services.DefectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/defect")
@RequiredArgsConstructor
@Tag(name = "Defect", description = "Defect management APIs - CRUD operations for defects associated with main issues")
public class DefectRestController {

    private final DefectService defectService;

    @Operation(
            summary = "Get Defect by ID",
            description = "Retrieve a specific defect by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Defect found",
                    content = @Content(schema = @Schema(implementation = DefectRespDto.class))),
            @ApiResponse(responseCode = "404", description = "Defect not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{defectId}")
    public DefectRespDto getDefectById(
            @Parameter(description = "ID of the defect to retrieve", required = true)
            @PathVariable @NotNull final UUID defectId) {
        log.info("Received GET request for defect with id: {}", defectId);
        return defectService.getDefectById(defectId);
    }

    @Operation(
            summary = "Create a new Defect",
            description = "Create a new defect associated with a main issue"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Defect created successfully",
                    content = @Content(schema = @Schema(implementation = DefectRespDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Main issue not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    public DefectRespDto saveDefect(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Defect data to create (must include valid mainIssueId)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = DefectReqDto.class))
            )
            @Valid @RequestBody final DefectReqDto dto) {
        log.info("Received POST request to create defect for main issue id: {}", dto.getMainIssueId());
        return defectService.saveDefect(dto);
    }

    @Operation(
            summary = "Update an existing Defect",
            description = "Update a defect to associate it with a different main issue"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Defect updated successfully",
                    content = @Content(schema = @Schema(implementation = DefectRespDto.class))),
            @ApiResponse(responseCode = "404", description = "Defect or Main issue not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{defectId}")
    public DefectRespDto updateDefect(
            @Parameter(description = "ID of the defect to update", required = true)
            @PathVariable @NotNull final UUID defectId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated defect data (must include valid mainIssueId)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = DefectReqDto.class))
            )
            @Valid @RequestBody final DefectReqDto dto) {
        log.info("Received PUT request to update defect with id: {}", defectId);
        return defectService.updateDefect(defectId, dto);
    }

    @Operation(
            summary = "Delete a Defect",
            description = "Delete a specific defect by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Defect deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Defect not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{defectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDefect(
            @Parameter(description = "ID of the defect to delete", required = true)
            @PathVariable @NotNull final UUID defectId) {
        log.info("Received DELETE request for defect with id: {}", defectId);
        defectService.deleteDefect(defectId);
    }
}

