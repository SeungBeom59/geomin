package com.geomin.demo.service;

import com.geomin.demo.domain.DepartmentVO;
import com.geomin.demo.domain.PatientVO;
import com.geomin.demo.domain.WaitingVO;
import com.geomin.demo.dto.DiagnosisDTO;
import com.geomin.demo.dto.PagingDTO;
import com.geomin.demo.dto.ResponseDTO;
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

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WaitingServiceImpl implements WaitingService {

    private final WaitingRepository waitingRepository;
    private final PatientRepository patientRepository;
    private final DiagnosisRepository diagnosisRepository;

    @Override
    public ResponseDTO getEndWaitingList(int page, int departmentId) {
        log.info("service단 page = " + page);

        int total = waitingRepository.getEndCount(departmentId);
        int size = 4;
        int btnCnt = 3;
        int offset = page * size;

        List<WaitingVO> waitingVOList = waitingRepository.getEndWaitingList(offset , departmentId);
        log.info("waitingVOList::{}", waitingVOList);

        // 페이징
        PagingDTO paging = PagingDTO.builder()
                .page(page + 1)
                .total(total)
                .size(size)
                .btnCnt(btnCnt)
                .build();

        paging.init();

        int waitingTotal = waitingRepository.getWaitingTotal(departmentId);
        List<WaitingDTO> waitingDTOList = new ArrayList<>();

        for (WaitingVO vo : waitingVOList) {

            String waitingDate = vo.getWaitingDate().toString();
            String time = waitingDate.substring(waitingDate.indexOf("T")+1);

            WaitingDTO dto = WaitingDTO.builder()
                    .waitingId(vo.getWaitingId())
                    .patientId(vo.getPatient().getPatientId())
                    .patientName(vo.getPatient().getPatientName())
                    .identify(WaitingUtil.getIdentify(vo.getPatient().getIdentify()))
                    .gender(vo.getPatient().isGender())
                    .age(vo.getPatient().getAge())
                    .departmentId(vo.getDepartment().getDepartmentId())
                    .waitingKey(vo.getWaitingKey())
                    .waitingDate(time)
                    .waitingStatus(WaitingUtil.getWaitingStatus(vo.getWaitingStatus()))
                    .waitingType(WaitingUtil.getWaitingType(vo.getWaitingType()))
                    .payStatus(vo.getPayStatus())
                    .build();

            waitingDTOList.add(dto);
        }

        if(!waitingDTOList.isEmpty()) {
            waitingDTOList.get(0).setWaitingEndCnt(waitingTotal);
        }

        ResponseDTO response = new ResponseDTO();
        response.setPagingDTO(paging);
        response.setWaitingDTOList(waitingDTOList);
        log.info("response::{}", response);

        return response;
    }

    //
    @Override
    public Page<WaitingDTO> getWaitingList(Pageable pageable , int departmentId) {

        long offset = pageable.getOffset();

        List<WaitingVO> result = waitingRepository.getWaitingList(offset , departmentId);

        log.info("result::{}" , result);
        log.info("***********************************************************");
        log.info("departmentId::{}" , departmentId);

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
            dto.setReceptionist(vo.getReceptionist());

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

        int total = waitingRepository.getWaitingTotal(departmentId);
        int waitingEndCnt = waitingRepository.getEndCount(departmentId);

        if(!content.isEmpty()){
            content.get(0).setWaitingEndCnt(waitingEndCnt);
        }

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
    public int addWaiting(WaitingDTO waitingDTO , Principal principal) {

        int lastWaitingId = waitingRepository.getLastWaitingId();   // 마지막 waitingId(pk) 가져오기
        waitingDTO.setLastWaitingId(lastWaitingId); // 마지막 pk 설정 +1 해서 sql로 집어넣을 것.

        String lastWaitingKey = waitingRepository.getLastWaitingKey();  // 마지막 waitingKey(유니크) 가져오기

        String newWaitingKey = WaitingUtil.createWaitingKey(lastWaitingKey);    // 새로운 유니크 키 생성

        waitingDTO.setWaitingKey(newWaitingKey);            // 새로운 유니크 키 dto에 등록
        waitingDTO.setReceptionist(principal.getName());    // 최초 접수자 등록

        log.info("dto::{}" , waitingDTO);
        int waitingResult = waitingRepository.addWaiting(waitingDTO);   // 접수 대기 추가

        // 생성했던 유니크 키로 방금 집어넣은 waiting 가져오기
        WaitingVO result = waitingRepository.getWaitingByKey(newWaitingKey);

        DiagnosisDTO diagnosisDTO = DiagnosisDTO.builder()
                .patientId(waitingDTO.getPatientId())
                .departmentId(waitingDTO.getDepartmentId())
                .symptoms(waitingDTO.getSymptoms())
                .waitingId(result.getWaitingId())
                .build();

        int lastDiagnosisId = diagnosisRepository.getLastDiagnosisId(); // diagnosis(진료기록) 마지막 pk 가져오기
        int diagnosisResult = diagnosisRepository.addOnlySymptoms(diagnosisDTO , lastDiagnosisId);  // 증상 기록만 넣어주기

        return diagnosisResult + waitingResult; // 2개의 결과 모두 각각의 insert 이므로 1 + 1 = 2의 값 리턴.

    }

    @Override
    public int modifyWaitingStatus(WaitingDTO waitingDTO) {

        WaitingVO vo = WaitingUtil.modifyWaitingStatusOrType(waitingDTO);

        log.info("vo::{}",vo);

        int result = 0;

        if(vo.getWaitingStatus() == -1){
            result = waitingRepository.updateWaitingType(vo);
        }
        else if(vo.getWaitingType() == -1 && vo.getWaitingStatus() == 5){

            diagnosisRepository.deleteDiagnosisByWaitingId(vo.getWaitingId());  // 증상 기록된 진료기록 논리삭제처리
            result = waitingRepository.updateWaitingStatus(vo);
        }
        else if(vo.getWaitingType() == -1){
            result = waitingRepository.updateWaitingStatus(vo);
        }


        return result;
    }

//    @Override
//    public WaitingVO getWaitingById(int waitingId) {
//
//        waitingRepository.getWaitingById(waitingId);
//
//
//        return null;
//    }





}
