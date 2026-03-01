package com.example.demo.mappers;

import com.example.demo.dto.req.MainIssueReqDto;
import com.example.demo.dto.resp.MainIssueRespDto;
import com.example.demo.entities.MainIssueEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface MainIssueMapper {

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "id", ignore = true)
    MainIssueEntity toEntity(MainIssueReqDto mainIssueReqDto);

    MainIssueRespDto toDto(MainIssueEntity mainIssue);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityWithReqDto(MainIssueReqDto dto, @MappingTarget MainIssueEntity mainIssue);
}
