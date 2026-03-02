package com.example.demo.rest;

import com.example.demo.dto.req.DefectReqDto;
import com.example.demo.dto.resp.DefectRespDto;
import com.example.demo.services.DefectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/defect")
@RequiredArgsConstructor
public class DefectRestController {

    private final DefectService defectService;

    @GetMapping("/{defectId}")
    public DefectRespDto getDefectById(@PathVariable @NotNull final UUID defectId) {
        log.info("Received GET request for defect with id: {}", defectId);
        return defectService.getDefectById(defectId);
    }

    @PostMapping
    public DefectRespDto saveDefect(@Valid @RequestBody final DefectReqDto dto) {
        log.info("Received POST request to create defect for main issue id: {}", dto.getMainIssueId());
        return defectService.saveDefect(dto);
    }

    @PutMapping("/{defectId}")
    public DefectRespDto updateDefect(@PathVariable @NotNull final UUID defectId, @Valid @RequestBody final DefectReqDto dto) {
        log.info("Received PUT request to update defect with id: {}", defectId);
        return defectService.updateDefect(defectId, dto);
    }

    @DeleteMapping("/{defectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDefect(@PathVariable @NotNull final UUID defectId) {
        log.info("Received DELETE request for defect with id: {}", defectId);
        defectService.deleteDefect(defectId);
    }
}

