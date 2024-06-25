package com.geomin.demo.repository;

import com.geomin.demo.domain.PatientVO;
import com.geomin.demo.domain.VitalsVO;
import com.geomin.demo.dto.PatientDTO;
import com.geomin.demo.dto.RequestList;
import com.geomin.demo.dto.VitalsDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PatientRepository {

    List<PatientVO> findByName(String name);

    // 이름으로 검색한 환자의 페이징된 정보 반환
    List<PatientVO> getPatientList(RequestList<?> requestList);

    // 이름으로 검색된 환자의 튜플 갯수 반환
    int getTotal(PatientDTO patientDTO);

    // 환자의 마지막 pk 번호 가져오기
    int getLastPatientId();

    // 특정 환자(patientId : pk) 정보 가져오기
    PatientVO getPatientById(int patientId);

    // 새로운 환자 등록
    int addPatient(PatientVO vo);
    // 이름과 주민등록번호로 환자 정보 가져오기
    PatientVO getPatient(PatientVO vo);

    // 특정 환자 pk로 정보 변경하기
    int updatePatient(PatientVO vo);

    // 환자의 활력징후 정보를 추출
    List<VitalsVO> getVitalsList(RequestList<?> requestList);

    // 환자 활력징후 검색 총 튜플 갯수 반환
    int getVitalsTotal(int patientId);

    // 환자의 활력징후 정보 추가
    int addVitals(VitalsDTO vitalsDTO);

    // 환자 활력징후 마지막 pk 번호 가져오기
    int geLastVitalId();

    // 특정 활력징후 vital_id로 정보 변경하기
    int updateVitals(VitalsDTO vitalsDTO);


}
