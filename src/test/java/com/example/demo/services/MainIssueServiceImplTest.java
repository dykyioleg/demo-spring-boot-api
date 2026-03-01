package com.example.demo.services;

import com.example.demo.dto.req.MainIssueReqDto;
import com.example.demo.dto.resp.MainIssueRespDto;
import com.example.demo.entities.MainIssueEntity;
import com.example.demo.mappers.MainIssueMapper;
import com.example.demo.repositories.MainIssueRepository;
import com.example.demo.util.MockBeanGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class MainIssueServiceImplTest {

    private final MockBeanGenerator mockBeanGenerator = new MockBeanGenerator();

    private MainIssueServiceImpl mainIssueService;

    @Mock
    private MainIssueRepository mainIssueRepository;

    @BeforeEach
    void setUp() {
        final MainIssueMapper mainIssueMapper = Mappers.getMapper(MainIssueMapper.class);
        mainIssueService = new MainIssueServiceImpl(mainIssueRepository, mainIssueMapper);
    }

    @Test
    void saveMainIssue() {
        // given
        MainIssueEntity mainIssue = mockBeanGenerator.createPopulatedBean(MainIssueEntity.class);
        MainIssueReqDto reqDto = mockBeanGenerator.createPopulatedBean(MainIssueReqDto.class);
        given(mainIssueRepository.saveAndFlush(any())).willReturn(mainIssue);
        // when
        MainIssueRespDto result = mainIssueService.saveMainIssue(reqDto);
        // then
        verifyNoMoreInteractions(mainIssueRepository);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(mainIssue.getId());
    }
}