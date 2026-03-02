package com.example.demo.services;

import com.example.demo.dto.req.DefectReqDto;
import com.example.demo.dto.resp.DefectRespDto;

import java.util.UUID;

public interface DefectService {

    DefectRespDto saveDefect(DefectReqDto defectReqDto);

    DefectRespDto updateDefect(UUID defectId, DefectReqDto dto);

    DefectRespDto getDefectById(UUID defectId);

    void deleteDefect(UUID defectId);
}

