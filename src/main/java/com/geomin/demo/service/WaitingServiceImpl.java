package com.geomin.demo.service;

import com.geomin.demo.domain.DepartmentVO;
import com.geomin.demo.domain.DiagnosisVO;
import com.geomin.demo.domain.PatientVO;
import com.geomin.demo.domain.WaitingVO;
import com.geomin.demo.dto.WaitingDTO;
import com.geomin.demo.repository.DiagnosisRepository;
import com.geomin.demo.repository.PatientRepository;
import com.geomin.demo.repository.WaitingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WaitingServiceImpl implements WaitingService {

    private final WaitingRepository waitingRepository;
    private final PatientRepository patientRepository;
    private final DiagnosisRepository diagnosisRepository;

    //
    @Override
    public Page<WaitingDTO> getWaitingList(Pageable pageable , int departmentId) {

        long offset = pageable.getOffset();

        List<WaitingVO> result = waitingRepository.getWaitingList(offset , departmentId);

//        log.info("result::{}" , result);

        List<WaitingDTO> content = new ArrayList<>();

        for(WaitingVO vo : result){
            WaitingDTO dto = new WaitingDTO();

            PatientVO patient = vo.getPatient();
            DepartmentVO department = vo.getDepartment();

            // dto의 접수정보
            String waitingDate = vo.getWaitingDate().toString();
            String time = waitingDate.substring(waitingDate.indexOf("T")+1);
            int waitingType = vo.getWaitingType();

            dto.setWaitingId(vo.getWaitingId());
            dto.setWaitingKey(vo.getWaitingKey());
            dto.setWaitingDate(time);
            dto.setWaitingStatus(WaitingUtil.getWaitingStatus(vo.getWaitingStatus()));
            dto.setWaitingType(WaitingUtil.getWaitingType(vo.getWaitingType()));

            // dto의 환자정보
            dto.setPatientId(patient.getPatientId());
            dto.setPatientName(patient.getPatientName());
            dto.setIdentify(WaitingUtil.getIdentify(patient.getIdentify()));
            dto.setGender(patient.isGender());
            dto.setAge(patient.getAge());

            // dto의 진료과 정보
            dto.setDepartmentId(department.getDepartmentId());
            dto.setDepartmentName(department.getDepartmentName());
            dto.setDepartmentPhone(department.getDepartmentPhone());

            content.add(dto);
        }

        int total = waitingRepository.getTotal(departmentId);

        return new PageImpl<>(content, pageable, total);
    }

    @Transactional(readOnly = true)
    @Override
    public int getEndCount(int departmentId) {

        int cnt = waitingRepository.getEndCount(departmentId);

        return cnt;
    }


    @Transactional
    @Override
    public int addWaiting(WaitingDTO waitingDTO) {

        int patientId = waitingDTO.getPatientId();
        int departmentId = waitingDTO.getDepartmentId();
        String symptoms = waitingDTO.getSymptoms();

        int lastDiagnosisId = diagnosisRepository.getLastDiagnosisId();
        int diagnosisResult = diagnosisRepository.addOnlySymptoms(patientId, departmentId, symptoms , lastDiagnosisId);

        int lastWaitingId = waitingRepository.getLastWaitingId();   // 마지막 waitingId(pk) 가져오기
        waitingDTO.setLastWaitingId(lastWaitingId);

        String lastWaitingKey = waitingRepository.getLastWaitingKey();  // 마지막 waitingKey(유니크) 가져오기
        waitingDTO.setWaitingKey(WaitingUtil.createWaitingKey(lastWaitingKey));

        log.info("들어간다!! 들어간다!!!");
        log.info("dto::{}" , waitingDTO);
        int waitingResult = waitingRepository.addWaiting(waitingDTO);   // 접수 대기 추가
        log.info("waitingResult = " + waitingResult);

        return diagnosisResult + waitingResult;

    }
}
