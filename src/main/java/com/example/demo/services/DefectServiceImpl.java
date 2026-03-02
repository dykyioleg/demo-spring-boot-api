package com.example.demo.services;

import com.example.demo.dto.req.DefectReqDto;
import com.example.demo.dto.resp.DefectRespDto;
import com.example.demo.entities.DefectEntity;
import com.example.demo.entities.MainIssueEntity;
import com.example.demo.mappers.DefectMapper;
import com.example.demo.repositories.DefectRepository;
import com.example.demo.repositories.MainIssueRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefectServiceImpl implements DefectService {
    private static final String NOT_FOUND_DEFECT = "Invalid defect id";
    private static final String NOT_FOUND_MAIN_ISSUE = "Invalid main issue id";

    // Log message constants
    private static final String LOG_SAVING_DEFECT = "Saving new defect for main issue id: {}";
    private static final String LOG_SAVED_DEFECT = "Successfully saved defect with id: {}";
    private static final String LOG_UPDATING_DEFECT = "Updating defect with id: {}";
    private static final String LOG_UPDATED_DEFECT = "Successfully updated defect with id: {}";
    private static final String LOG_RETRIEVING_DEFECT = "Retrieving defect with id: {}";
    private static final String LOG_RETRIEVED_DEFECT = "Successfully retrieved defect with id: {}";
    private static final String LOG_DELETING_DEFECT = "Deleting defect with id: {}";
    private static final String LOG_DELETED_DEFECT = "Successfully deleted defect with id: {}";
    private static final String LOG_DEFECT_NOT_FOUND = "Defect not found with id: {}";
    private static final String LOG_MAIN_ISSUE_NOT_FOUND = "Main issue not found with id: {}";

    private final DefectRepository defectRepository;
    private final MainIssueRepository mainIssueRepository;
    private final DefectMapper defectMapper;

    @Override
    @Transactional
    public DefectRespDto saveDefect(final DefectReqDto defectReqDto) {
        log.info(LOG_SAVING_DEFECT, defectReqDto.getMainIssueId());

        final MainIssueEntity mainIssue = mainIssueRepository.findById(defectReqDto.getMainIssueId())
                .orElseThrow(() -> {
                    log.warn(LOG_MAIN_ISSUE_NOT_FOUND, defectReqDto.getMainIssueId());
                    return new EntityNotFoundException(NOT_FOUND_MAIN_ISSUE);
                });

        DefectEntity defect = defectMapper.toEntity(defectReqDto);
        defect.setMainIssue(mainIssue);
        DefectEntity savedDefect = defectRepository.saveAndFlush(defect);
        log.info(LOG_SAVED_DEFECT, savedDefect.getId());
        return defectMapper.toDto(savedDefect);
    }

    @Override
    @Transactional
    public DefectRespDto updateDefect(final UUID defectId, final DefectReqDto dto) {
        log.info(LOG_UPDATING_DEFECT, defectId);

        final DefectEntity defect = defectRepository.findById(defectId)
                .orElseThrow(() -> {
                    log.warn(LOG_DEFECT_NOT_FOUND, defectId);
                    return new EntityNotFoundException(NOT_FOUND_DEFECT);
                });

        // Verify that the main issue exists
        final MainIssueEntity mainIssue = mainIssueRepository.findById(dto.getMainIssueId())
                .orElseThrow(() -> {
                    log.warn(LOG_MAIN_ISSUE_NOT_FOUND, dto.getMainIssueId());
                    return new EntityNotFoundException(NOT_FOUND_MAIN_ISSUE);
                });

        defect.setMainIssue(mainIssue);
        log.info(LOG_UPDATED_DEFECT, defectId);
        return defectMapper.toDto(defect);
    }

    @Override
    public DefectRespDto getDefectById(final UUID defectId) {
        log.info(LOG_RETRIEVING_DEFECT, defectId);
        final DefectEntity defect = defectRepository.findById(defectId)
                .orElseThrow(() -> {
                    log.warn(LOG_DEFECT_NOT_FOUND, defectId);
                    return new EntityNotFoundException(NOT_FOUND_DEFECT);
                });
        log.debug(LOG_RETRIEVED_DEFECT, defectId);
        return defectMapper.toDto(defect);
    }

    @Override
    @Transactional
    public void deleteDefect(final UUID defectId) {
        log.info(LOG_DELETING_DEFECT, defectId);
        final DefectEntity defect = defectRepository.findById(defectId)
                .orElseThrow(() -> {
                    log.warn(LOG_DEFECT_NOT_FOUND, defectId);
                    return new EntityNotFoundException(NOT_FOUND_DEFECT);
                });
        defectRepository.delete(defect);
        log.info(LOG_DELETED_DEFECT, defectId);
    }


    @Override
    public List<DefectRespDto> getDefectsByMainIssueIds(final List<UUID> mainIssueIds) {
        if (mainIssueIds == null || mainIssueIds.isEmpty()) {
            log.warn("Empty or null main issue IDs list provided");
            return List.of();
        }

        log.info("Retrieving defects for {} main issues (avoiding N+1 problem)", mainIssueIds.size());

        final List<DefectEntity> defects = defectRepository.findByMainIssueIdIn(mainIssueIds);

        log.info("Successfully retrieved {} defects for {} main issues in a single query", defects.size(), mainIssueIds.size());

        return defects.stream()
                .map(defectMapper::toDto)
                .collect(Collectors.toList());
    }
}
