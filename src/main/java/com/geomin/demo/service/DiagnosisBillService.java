package com.geomin.demo.service;

import com.geomin.demo.domain.BillCalculateResult;
import com.geomin.demo.dto.DiagnosisDTO;
import com.geomin.demo.dto.ResponseDTO;

// 진료 수납 내역
public interface DiagnosisBillService {

    // 수납내역 생성
    int createDiagnosisBill(ResponseDTO responseDTO);

    // 수납내역 수정
    int updateDiagnosisBill(BillCalculateResult billResult);
}
