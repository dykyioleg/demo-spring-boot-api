package com.example.demo.services;

import com.example.demo.dto.req.MainIssueReqDto;
import com.example.demo.dto.resp.MainIssueRespDto;
import com.example.demo.entities.MainIssueEntity;
import com.example.demo.mappers.MainIssueMapper;
import com.example.demo.repositories.MainIssueRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainIssueServiceImpl implements MainIssueService {
    private static final String NOT_FOUND_MAIN_ISSUE = "Invalid main issue id";

    private final MainIssueRepository mainIssueRepository;
    private final MainIssueMapper mainIssueMapper;

    @Override
    @Transactional(readOnly = false)
    public MainIssueRespDto saveMainIssue(final MainIssueReqDto mainIssueReqDto){
        MainIssueEntity mainIssue = mainIssueMapper.toEntity(mainIssueReqDto);
        MainIssueEntity savedMainIssue = mainIssueRepository.saveAndFlush(mainIssue);
        return mainIssueMapper.toDto(savedMainIssue);
    }

    @Override
    @Transactional(readOnly = false)
    public MainIssueRespDto updateMainIssue(final UUID mainIssueId, final MainIssueReqDto dto) {
        final MainIssueEntity mainIssue = mainIssueRepository.findById(mainIssueId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MAIN_ISSUE));
        mainIssueMapper.updateEntityWithReqDto(dto, mainIssue);
        return mainIssueMapper.toDto(mainIssue);
    }
}
