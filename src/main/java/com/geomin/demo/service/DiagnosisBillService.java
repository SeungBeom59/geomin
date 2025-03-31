package com.geomin.demo.service;

import com.geomin.demo.dto.DiagnosisDTO;

// 진료 수납 내역
public interface DiagnosisBillService {

    // 수납내역 생성
    int createDiagnosisBill(DiagnosisDTO diagnosisDTO);

    // 수납내역 수정
    int updateDiagnosisBill();
}
