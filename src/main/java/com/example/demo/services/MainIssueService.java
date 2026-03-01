package com.example.demo.services;

import com.example.demo.dto.req.MainIssueReqDto;
import com.example.demo.dto.resp.MainIssueRespDto;

import java.util.UUID;

public interface MainIssueService {

    MainIssueRespDto saveMainIssue(MainIssueReqDto mainIssueReqDto);

    MainIssueRespDto updateMainIssue(UUID mainIssueId, MainIssueReqDto dto);

}
