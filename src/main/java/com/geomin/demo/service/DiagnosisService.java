package com.geomin.demo.service;

import com.geomin.demo.dto.DiagnosisDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DiagnosisService {

    Page<DiagnosisDTO> getDiagnosisList(Pageable pageable , DiagnosisDTO diagnosisDTO);


    DiagnosisDTO getTodayDiagnosis(int waitingId);
}
