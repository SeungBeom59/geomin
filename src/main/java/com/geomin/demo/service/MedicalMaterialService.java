package com.geomin.demo.service;

import com.geomin.demo.dto.MedicalMaterialDTO;

import java.util.List;

public interface MedicalMaterialService {
    // 치료재료 정보 keyword 판별하여 검색하기
    public List<MedicalMaterialDTO> searchMedicalMaterial(String keyword);

    // 치료재료청구 테이블에 insert 하기
    int insertMedicalBills(List<MedicalMaterialDTO> medicals);

    // 치료재료청구 테이블의 검색되는 pk 레코드 삭제 후, 새로운 값으로 insert 또는 비우기.
    int deleteAndCreateMedicalBill(int medicalBillId, List<MedicalMaterialDTO> medicals);

    // 치료재료청구 pk로 관련된 레코드들을 가져온다
    List<MedicalMaterialDTO> getMaterialByBillId(int medicalBillId);
}
