package com.geomin.demo.service;

import com.geomin.demo.domain.BillCalculateResult;
import com.geomin.demo.dto.DiagnosisDTO;

// 수납 명세를 위한 진료비 계산
public interface BillCalculateService {

    // 클라이언트인 diangosisBillService에서는 아래 메소드로 계산한다는 것만 알면 됨.
    BillCalculateResult calculateBill(DiagnosisDTO diagnosisDTO);
}
