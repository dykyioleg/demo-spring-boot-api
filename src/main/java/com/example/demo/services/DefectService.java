package com.example.demo.services;

import com.example.demo.dto.req.DefectReqDto;
import com.example.demo.dto.resp.DefectRespDto;

import java.util.List;
import java.util.UUID;

public interface DefectService {

    DefectRespDto saveDefect(DefectReqDto defectReqDto);

    DefectRespDto updateDefect(UUID defectId, DefectReqDto dto);

    DefectRespDto getDefectById(UUID defectId);

    void deleteDefect(UUID defectId);

    /**
     * Retrieve defects for specific main issues.
     * Uses JOIN FETCH to avoid N+1 query problem.
     *
     * @param mainIssueIds List of main issue IDs to fetch defects for
     * @return List of defects with eagerly loaded main issues
     */
    List<DefectRespDto> getDefectsByMainIssueIds(List<UUID> mainIssueIds);
}
