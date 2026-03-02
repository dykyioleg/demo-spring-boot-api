package com.example.demo.services;

import com.example.demo.dto.req.MainIssueReqDto;
import com.example.demo.dto.resp.MainIssueRespDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MainIssueService {

    MainIssueRespDto saveMainIssue(MainIssueReqDto mainIssueReqDto);

    MainIssueRespDto updateMainIssue(UUID mainIssueId, MainIssueReqDto dto);

    MainIssueRespDto getMainIssueById(UUID mainIssueId);

    void deleteMainIssue(UUID mainIssueId);

    Page<MainIssueRespDto> getAllMainIssues(Pageable pageable);

}
