package com.geomin.demo.service;

import com.geomin.demo.dto.DiagnosisDTO;
import com.geomin.demo.dto.ResponseDTO;

public interface DiagnosisService {

//    Page<DiagnosisDTO> getDiagnosisList(Pageable pageable , DiagnosisDTO diagnosisDTO);

    ResponseDTO getDiagnosisList(int page , DiagnosisDTO diagnosisDTO);

    DiagnosisDTO getTodayDiagnosis(int waitingId);

    ResponseDTO updateDiagnosisById(DiagnosisDTO diagnosisDTO);

    ResponseDTO createDiagnosis(DiagnosisDTO diagnosisDTO);

    ResponseDTO getDiagnosisById(int diagnosisId);

    boolean deleteDiagnosisById(int diagnosisId);

    // 과거 미작성한 진료기록 논리 삭제
    int deleteOldNotFinishedDiagnosis();
}
