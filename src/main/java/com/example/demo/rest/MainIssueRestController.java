package com.example.demo.rest;

import com.example.demo.dto.req.MainIssueReqDto;
import com.example.demo.dto.resp.MainIssueRespDto;
import com.example.demo.services.MainIssueService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/main-issue")
@RequiredArgsConstructor
public class MainIssueRestController {

	private final MainIssueService mainIssueService;

	@GetMapping("/{mainIssueId}")
	public MainIssueRespDto getMainIssueById(@PathVariable @NotNull final UUID mainIssueId) {
		log.info("Received GET request for main issue with id: {}", mainIssueId);
		return mainIssueService.getMainIssueById(mainIssueId);
	}

	@PostMapping
	public MainIssueRespDto saveMainIssue(@Valid @RequestBody final MainIssueReqDto dto) {
		log.info("Received POST request to create main issue with description: {}", dto.getDescription());
		return mainIssueService.saveMainIssue(dto);
	}

	@PutMapping("/{mainIssueId}")
	public MainIssueRespDto updateMainIssue(@PathVariable @NotNull final UUID mainIssueId, @Valid @RequestBody final MainIssueReqDto dto) {
		log.info("Received PUT request to update main issue with id: {}", mainIssueId);
		return mainIssueService.updateMainIssue(mainIssueId, dto);
	}
}