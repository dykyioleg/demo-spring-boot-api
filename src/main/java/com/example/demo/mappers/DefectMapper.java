package com.example.demo.mappers;

import com.example.demo.dto.req.DefectReqDto;
import com.example.demo.dto.resp.DefectRespDto;
import com.example.demo.entities.DefectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface DefectMapper {

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mainIssue", ignore = true)
    DefectEntity toEntity(DefectReqDto defectReqDto);

    @Mapping(target = "mainIssueId", source = "mainIssue.id")
    DefectRespDto toDto(DefectEntity defect);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mainIssue", ignore = true)
    void updateEntityWithReqDto(DefectReqDto dto, @MappingTarget DefectEntity defect);
}



