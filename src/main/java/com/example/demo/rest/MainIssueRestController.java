package com.example.demo.rest;

import com.example.demo.dto.req.MainIssueReqDto;
import com.example.demo.dto.resp.MainIssueRespDto;
import com.example.demo.services.MainIssueService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/main-issue")
@RequiredArgsConstructor
public class MainIssueRestController {

	private final MainIssueService mainIssueService;

	@GetMapping("/hello")
	public String helloWorld() {
		return "Hello world!";
	}

	@PostMapping
	public MainIssueRespDto saveMainIssue(@Valid @RequestBody final MainIssueReqDto dto) {
		return mainIssueService.saveMainIssue(dto);
	}

	@PutMapping("/{mainIssueId}")
	public MainIssueRespDto updateMainIssue(@PathVariable @NotNull final UUID mainIssueId, @Valid @RequestBody final MainIssueReqDto dto) {
		return mainIssueService.updateMainIssue(mainIssueId, dto);
	}
}