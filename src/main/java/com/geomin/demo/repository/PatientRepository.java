package com.geomin.demo.repository;

import com.geomin.demo.domain.PatientVO;
import com.geomin.demo.domain.VitalsVO;
import com.geomin.demo.dto.PatientDTO;
import com.geomin.demo.dto.RequestList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PatientRepository {

    List<PatientVO> findByName(String name);

    // 이름으로 검색한 환자의 페이징된 정보 반환
    List<PatientVO> getPatientList(RequestList<?> requestList);
    // 이름으로 검색된 환자의 튜플 갯수 반환
    int getTotal(PatientDTO patientDTO);
    // 환자의 활력징후 정보를 추출
    List<VitalsVO> getVitalsList(RequestList<?> requestList);
    // 환자 활력징후 검색 총 튜플 갯수 반환
    int getVitalsTotal(int patientId);
}
