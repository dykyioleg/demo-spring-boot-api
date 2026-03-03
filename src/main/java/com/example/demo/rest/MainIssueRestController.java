package com.example.demo.rest;

import com.example.demo.dto.req.MainIssueReqDto;
import com.example.demo.dto.resp.MainIssueRespDto;
import com.example.demo.services.MainIssueService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing Main Issue entities.
 * <p>
 * This controller provides CRUD operations for main issues with the following features:
 * <ul>
 *   <li>Create, Read, Update, and Delete operations for main issues</li>
 *   <li>JWT authentication required for POST operations (create new main issue)</li>
 *   <li>Cascade deletion - deleting a main issue automatically deletes all related defects</li>
 *   <li>Input validation using Jakarta Bean Validation</li>
 *   <li>RFC 7807 Problem Details for error responses</li>
 * </ul>
 * <p>
 * Base path: {@code /api/main-issue}
 * <p>
 * Authentication: Only POST endpoint requires JWT token with valid issuer and audience claims.
 * All other endpoints (GET, PUT, DELETE) are publicly accessible.
 *
 * @see MainIssueService
 * @see MainIssueReqDto
 * @see MainIssueRespDto
 * @author Demo Project
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/main-issue")
@RequiredArgsConstructor
@Tag(name = "Main Issue", description = "Main Issue management APIs - CRUD operations for main issues and cascade deletion of related defects")
public class MainIssueRestController {

	private final MainIssueService mainIssueService;

	@Operation(
			summary = "Get Main Issue by ID",
			description = "Retrieve a specific main issue by its unique identifier"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Main issue found",
					content = @Content(schema = @Schema(implementation = MainIssueRespDto.class))),
			@ApiResponse(responseCode = "404", description = "Main issue not found",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
	})
	@GetMapping("/{mainIssueId}")
	public MainIssueRespDto getMainIssueById(
			@Parameter(description = "ID of the main issue to retrieve", required = true)
			@PathVariable @NotNull final UUID mainIssueId) {
		log.info("Received GET request for main issue with id: {}", mainIssueId);
		return mainIssueService.getMainIssueById(mainIssueId);
	}

	@Operation(
			summary = "Get all Main Issues with Pagination",
			description = "Retrieve all main issues with pagination and sorting support. " +
					"Default page size is 20, sorted by creation date in descending order. " +
					"Example: GET /api/main-issue?page=0&size=10&sort=created,desc"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Main issues retrieved successfully",
					content = @Content(schema = @Schema(implementation = Page.class)))
	})
	@GetMapping
	public Page<MainIssueRespDto> getAllMainIssues(
			@Parameter(description = "Pagination parameters (page, size, sort)", example = "page=0&size=20&sort=created,desc")
			@PageableDefault(size = 20, sort = "created", direction = Sort.Direction.DESC)
			Pageable pageable) {
		log.info("Received GET request for all main issues with pagination: page={}, size={}",
				pageable.getPageNumber(), pageable.getPageSize());
		return mainIssueService.getAllMainIssues(pageable);
	}

	@Operation(
			summary = "Create a new Main Issue",
			description = "Create a new main issue with the provided details. Requires valid JWT token with correct issuer and audience."
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Main issue created successfully",
					content = @Content(schema = @Schema(implementation = MainIssueRespDto.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input data",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
			@ApiResponse(responseCode = "403", description = "Forbidden - Token does not have required permissions",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
	})
	@PreAuthorize("isAuthenticated()")
	@PostMapping
	public MainIssueRespDto saveMainIssue(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Main issue data to create",
					required = true,
					content = @Content(schema = @Schema(implementation = MainIssueReqDto.class))
			)
			@Valid @RequestBody final MainIssueReqDto dto) {
		log.info("Received POST request to create main issue with description: {}", dto.getDescription());
		return mainIssueService.saveMainIssue(dto);
	}

	@Operation(
			summary = "Update an existing Main Issue",
			description = "Update an existing main issue with the provided details"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Main issue updated successfully",
					content = @Content(schema = @Schema(implementation = MainIssueRespDto.class))),
			@ApiResponse(responseCode = "404", description = "Main issue not found",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input data",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
	})
	@PutMapping("/{mainIssueId}")
	public MainIssueRespDto updateMainIssue(
			@Parameter(description = "ID of the main issue to update", required = true)
			@PathVariable @NotNull final UUID mainIssueId,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Updated main issue data",
					required = true,
					content = @Content(schema = @Schema(implementation = MainIssueReqDto.class))
			)
			@Valid @RequestBody final MainIssueReqDto dto) {
		log.info("Received PUT request to update main issue with id: {}", mainIssueId);
		return mainIssueService.updateMainIssue(mainIssueId, dto);
	}

	@Operation(
			summary = "Delete a Main Issue",
			description = "Delete a main issue and all its related defects (cascade deletion)"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Main issue and related defects deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Main issue not found",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
	})
	@DeleteMapping("/{mainIssueId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMainIssue(
			@Parameter(description = "ID of the main issue to delete", required = true)
			@PathVariable @NotNull final UUID mainIssueId) {
		log.info("Received DELETE request for main issue with id: {}", mainIssueId);
		mainIssueService.deleteMainIssue(mainIssueId);
	}
}