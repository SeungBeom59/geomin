package com.geomin.demo.repository;

import com.geomin.demo.domain.TreatmentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
// 처방수가 매퍼
public interface TreatmentRepository {

    // 처방수가 마지막 pk 번호 가져오기
    int getLastTreatmentId();
    // 처방수가 기록 넣기
    int insertTreatments(List<TreatmentVO> treatmentVOs);
    // 처방수가 pk 번호로 관련 레코드 모두 삭제
    void deleteById(int treatmentId);
    // 처방수가 pk 번호로 관련 레코드 모두 가져와 리스트 반환
    List<TreatmentVO> getTreatmentById(int treatmentId);
}
