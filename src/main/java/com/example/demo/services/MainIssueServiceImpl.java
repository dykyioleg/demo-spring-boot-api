package com.example.demo.services;

import com.example.demo.dto.req.MainIssueReqDto;
import com.example.demo.dto.resp.MainIssueRespDto;
import com.example.demo.entities.DefectEntity;
import com.example.demo.entities.MainIssueEntity;
import com.example.demo.mappers.MainIssueMapper;
import com.example.demo.repositories.DefectRepository;
import com.example.demo.repositories.MainIssueRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainIssueServiceImpl implements MainIssueService {
    private static final String NOT_FOUND_MAIN_ISSUE = "Invalid main issue id";

    // Log message constants
    private static final String LOG_SAVING_MAIN_ISSUE = "Saving new main issue with description: {}";
    private static final String LOG_SAVED_MAIN_ISSUE = "Successfully saved main issue with id: {}";
    private static final String LOG_UPDATING_MAIN_ISSUE = "Updating main issue with id: {}";
    private static final String LOG_UPDATED_MAIN_ISSUE = "Successfully updated main issue with id: {}";
    private static final String LOG_RETRIEVING_MAIN_ISSUE = "Retrieving main issue with id: {}";
    private static final String LOG_RETRIEVED_MAIN_ISSUE = "Successfully retrieved main issue with id: {}";
    private static final String LOG_MAIN_ISSUE_NOT_FOUND = "Main issue not found with id: {}";
    private static final String LOG_DELETING_MAIN_ISSUE = "Deleting main issue with id: {}";
    private static final String LOG_DELETING_RELATED_DEFECTS = "Deleting {} related defects for main issue id: {}";
    private static final String LOG_DELETED_MAIN_ISSUE = "Successfully deleted main issue with id: {}";
    private static final String LOG_RETRIEVING_ALL_MAIN_ISSUES = "Retrieving all main issues: page={}, size={}, sort={}";
    private static final String LOG_RETRIEVED_ALL_MAIN_ISSUES = "Successfully retrieved {} main issues out of {} total";

    private final MainIssueRepository mainIssueRepository;
    private final MainIssueMapper mainIssueMapper;
    private final DefectRepository defectRepository;

    @Override
    @Transactional(readOnly = false)
    public MainIssueRespDto saveMainIssue(final MainIssueReqDto mainIssueReqDto){
        log.info(LOG_SAVING_MAIN_ISSUE, mainIssueReqDto.getDescription());
        MainIssueEntity mainIssue = mainIssueMapper.toEntity(mainIssueReqDto);
        MainIssueEntity savedMainIssue = mainIssueRepository.saveAndFlush(mainIssue);
        log.info(LOG_SAVED_MAIN_ISSUE, savedMainIssue.getId());
        return mainIssueMapper.toDto(savedMainIssue);
    }

    @Override
    @Transactional(readOnly = false)
    public MainIssueRespDto updateMainIssue(final UUID mainIssueId, final MainIssueReqDto dto) {
        log.info(LOG_UPDATING_MAIN_ISSUE, mainIssueId);
        final MainIssueEntity mainIssue = mainIssueRepository.findById(mainIssueId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MAIN_ISSUE));
        mainIssueMapper.updateEntityWithReqDto(dto, mainIssue);
        log.info(LOG_UPDATED_MAIN_ISSUE, mainIssueId);
        return mainIssueMapper.toDto(mainIssue);
    }

    @Override
    public MainIssueRespDto getMainIssueById(final UUID mainIssueId) {
        log.info(LOG_RETRIEVING_MAIN_ISSUE, mainIssueId);
        final MainIssueEntity mainIssue = mainIssueRepository.findById(mainIssueId)
                .orElseThrow(() -> {
                    log.warn(LOG_MAIN_ISSUE_NOT_FOUND, mainIssueId);
                    return new EntityNotFoundException(NOT_FOUND_MAIN_ISSUE);
                });
        log.debug(LOG_RETRIEVED_MAIN_ISSUE, mainIssueId);
        return mainIssueMapper.toDto(mainIssue);
    }

    @Override
    @Transactional
    public void deleteMainIssue(final UUID mainIssueId) {
        log.info(LOG_DELETING_MAIN_ISSUE, mainIssueId);

        // Verify main issue exists
        final MainIssueEntity mainIssue = mainIssueRepository.findById(mainIssueId)
                .orElseThrow(() -> {
                    log.warn(LOG_MAIN_ISSUE_NOT_FOUND, mainIssueId);
                    return new EntityNotFoundException(NOT_FOUND_MAIN_ISSUE);
                });

        // Find and delete all related defects first
        final List<DefectEntity> relatedDefects = defectRepository.findByMainIssueId(mainIssueId);
        if (!relatedDefects.isEmpty()) {
            log.info(LOG_DELETING_RELATED_DEFECTS, relatedDefects.size(), mainIssueId);
            defectRepository.deleteAll(relatedDefects);
        }

        // Delete the main issue
        mainIssueRepository.delete(mainIssue);
        log.info(LOG_DELETED_MAIN_ISSUE, mainIssueId);
    }

    @Override
    public Page<MainIssueRespDto> getAllMainIssues(final Pageable pageable) {
        log.info(LOG_RETRIEVING_ALL_MAIN_ISSUES, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<MainIssueEntity> mainIssuePage = mainIssueRepository.findAll(pageable);

        log.info(LOG_RETRIEVED_ALL_MAIN_ISSUES, mainIssuePage.getNumberOfElements(), mainIssuePage.getTotalElements());

        return mainIssuePage.map(mainIssueMapper::toDto);
    }
}
