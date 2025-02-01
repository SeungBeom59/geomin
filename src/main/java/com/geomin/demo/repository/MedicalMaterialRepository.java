package com.geomin.demo.repository;

import com.geomin.demo.domain.MedicalBillVO;
import com.geomin.demo.domain.MedicalMaterialVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MedicalMaterialRepository {

    // 치료재료명으로 검색하기 (LIMIT 10개)
    List<MedicalMaterialVO> getMedicalByName(String keyword);

    // 치료재료코드로 검색하기 (LIMIT 10개)
    List<MedicalMaterialVO> getMedicalByCode(String keyword);

    // 치료재료청구 테이블에 치료재료 정보 insert 하기
    int insertMedicalBills(List<MedicalBillVO> medicalBillVOS);

    // 치료재료청구 테이블에서 마지막 pk 번호 가져오기
    int getLastMedicalBillId();

    // 치료재료청구 테이블에서 기록 삭제
    void deleteByMedicalBillId(int medicalBillId);

    // 치료재료청구 pk로 레코드 조회하여, 해당 레코드의 참조되는 치료재료 목록 가져오기
    List<MedicalBillVO> getMaterialByBillId(int medicalBillId);
}
