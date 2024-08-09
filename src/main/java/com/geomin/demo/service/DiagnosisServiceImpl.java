package com.geomin.demo.service;

import com.geomin.demo.domain.*;
import com.geomin.demo.dto.DiagnosisDTO;
import com.geomin.demo.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiagnosisServiceImpl implements DiagnosisService{

    private final DiagnosisRepository diagnosisRepository;

    @Override
    public Page<DiagnosisDTO> getDiagnosisList(Pageable pageable , DiagnosisDTO diagnosisDTO) {

        Sort sort = diagnosisDTO.isSort() ?
                Sort.by(Sort.Order.desc("diagnosis_date")) : Sort.by(Sort.Order.asc("diagnosis_date"));

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        List<DiagnosisVO> result = diagnosisRepository.getDiagnosisList(pageable , diagnosisDTO);

        log.info("result::{}",result);

        List<DiagnosisDTO> content = new ArrayList<>();

        for(DiagnosisVO vo : result){
            DiagnosisDTO dto = new DiagnosisDTO();

            PatientVO patient = vo.getPatient();
            DoctorVO doctor = vo.getDoctor();
            DepartmentVO department = vo.getDepartment();

            dto = vo.getDateAndModifiedMember(vo, dto);

//            String diagnosisDate = vo.getDiagnosisDate().toString();
//            String time =  diagnosisDate.replace("T" , "/");
//
//            dto.setDiagnosisDate(time);
//
//            if(vo.getModifyDate() != null && vo.getDiagnosisModifier() != null) {
//                String modifiedDate = vo.getModifyDate().toString();
//                String modifiedTime = modifiedDate.replace("T" , "/");
//                dto.setModifyDate(modifiedTime);
//
//                dto.setDiagnosisModifier(vo.getDiagnosisModifier());
//            }

            dto.setDiagnosisId(vo.getDiagnosisId());

            dto.setPatientId(patient.getPatientId());
            dto.setPatientName(patient.getPatientName());
            dto.setDoctorId(doctor.getDoctorId());
            dto.setDoctorName(doctor.getDoctorName());
            dto.setDepartmentId(department.getDepartmentId());
            dto.setDepartmentName(department.getDepartmentName());

            dto.setSymptoms(vo.getSymptoms());
            dto.setDiagnosis(vo.getDiagnosis());
            dto.setPrescription(vo.getPrescription());

            dto.setFileId(vo.getFileId());

            content.add(dto);
        }

        log.info("content::{}", content);

        int total = diagnosisRepository.getTotalDiagnosis(diagnosisDTO);

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public DiagnosisDTO getTodayDiagnosis(int waitingId) {

        DiagnosisVO result = diagnosisRepository.getDiagnosisByWaitingId(waitingId);

        log.info("result::{}" , result);

        PatientVO patient = result.getPatient();
        DepartmentVO department = result.getDepartment();
        WaitingVO waiting = result.getWaiting();

        log.info("patient::{}" , patient);

        DiagnosisDTO todayDiagnosis = DiagnosisDTO.builder()
                .diagnosisId(result.getDiagnosisId())
                .patientId(patient.getPatientId())
                .patientName(patient.getPatientName())
                .departmentId(department.getDepartmentId())
                .departmentName(department.getDepartmentName())
                .waitingId(waiting.getWaitingId())
                .symptoms(result.getSymptoms())
                .diagnosis(result.getDiagnosis())
                .prescription(result.getPrescription())
                .diagnosisYn(result.getDiagnosisYn())
                .fileId(result.getFileId())
                .build();

        todayDiagnosis = result.getDateAndModifiedMember(result , todayDiagnosis);

        return todayDiagnosis;
    }

}
