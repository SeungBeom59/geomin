package com.geomin.demo.repository;

import com.geomin.demo.domain.DiagnosisVO;
import com.geomin.demo.dto.DiagnosisDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
public interface DiagnosisRepository {

    int getLastDiagnosisId();

    int addOnlySymptoms(DiagnosisDTO diagnosisDTO, int lastDiagnosisId);

    List<DiagnosisVO> getDiagnosisList(Pageable pageable , DiagnosisDTO diagnosisDTO);


    int getTotalDiagnosis(DiagnosisDTO diagnosisDTO);

    void deleteDiagnosisByWaitingId(int waitingId);

    DiagnosisVO getDiagnosisByWaitingId(int waitingId);

    int updateDiagnosisById(DiagnosisDTO diagnosisDTO);

    DiagnosisVO getDiagnosisById(int diagnosisId);
}
