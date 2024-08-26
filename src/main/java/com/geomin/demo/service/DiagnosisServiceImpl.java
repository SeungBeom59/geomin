package com.geomin.demo.service;

import com.geomin.demo.domain.*;
import com.geomin.demo.dto.*;
import com.geomin.demo.repository.DiagnosisRepository;
import com.geomin.demo.repository.WaitingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiagnosisServiceImpl implements DiagnosisService{

    private final DiagnosisRepository diagnosisRepository;
    private final FileService fileService;
    private final MedicineService medicineService;
    private final WaitingService waitingService;

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
    @Transactional
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

    @Override
    @Transactional
    public ResponseDTO updateDiagnosisById(DiagnosisDTO diagnosisDTO) {
        log.info("updateDiagnosisById 실행");
        log.info("diagnosisDTO::{}" , diagnosisDTO);

        // 진료기록에 대한 업데이트 진행
        int updateResult = diagnosisRepository.updateDiagnosisById(diagnosisDTO);

        // 만약 진료기록이 업데이트 되지 못했다면...
        if(updateResult <= 0){
            return null;
        }


        // 파일정보, 의약품 처방 정보, 진료기록 정보를 모두 감싸는 responseDTO 생성
        ResponseDTO responseDTO = new ResponseDTO();
        // 진료기록을 id를 통해 가져오고.
        DiagnosisVO result = diagnosisRepository.getDiagnosisById(diagnosisDTO.getDiagnosisId());

        // 진료기록의 진료접수 id를 이용하여 진료접수를 가져오고
        if(result.getWaiting() != null && result.getWaiting().getWaitingStatus() != 2 ){

            WaitingDTO changeWaitingStatus = WaitingDTO.builder()
                    .waitingId(result.getWaiting().getWaitingId())
                    .action("진료완료")
                    .build();

            waitingService.modifyWaitingStatus(changeWaitingStatus);
        }

        // 해당 진료기록의 파일 정보 id가 있다면 따로 가져와서 responseDTO에 넣어준다.
        // 외래키로 사용은 하나 관계는 맺지 않았음. join을 해서 가져오자니,
        // 파일정보가 몇개의 list 형태일지 모르겠다는 점과 더불어, 검색된 파일정보의 수 만큼 진료기록의 결과가 곱해져서 나오게 됨.
        // 아래 의약품도 이와 마찬가지임.
        if(result != null && result.getFileId() > 0){
            List<FileInfoDTO> fileList = fileService.getFileById(result.getFileId());
            responseDTO.setFileInfoDTOList(fileList);
        }

        if(result != null && result.getMedicineId() > 0){
            List<MedicineDTO> medicines = medicineService.getMedicineListById(result.getMedicineId());
            responseDTO.setMedicineDTOList(medicines);
        }

        PatientVO patient = result.getPatient();
        DepartmentVO department = result.getDepartment();
        WaitingVO waiting = result.getWaiting();
        DoctorVO doctor = result.getDoctor();

        log.info("result::{}" , result);

        DiagnosisDTO diagnosis = DiagnosisDTO.builder()
                .diagnosisId(result.getDiagnosisId())
                .patientId(patient.getPatientId())
                .patientName(patient.getPatientName())
                .doctorId(doctor.getDoctorId())
                .doctorName(doctor.getDoctorName())
                .departmentId(department.getDepartmentId())
                .departmentName(department.getDepartmentName())
                .waitingId(waiting.getWaitingId())
                .symptoms(result.getSymptoms())
                .diagnosis(result.getDiagnosis())
                .prescription(result.getPrescription())
                .diagnosisYn(result.getDiagnosisYn())
                .fileId(result.getFileId())
                .build();

        diagnosis = result.getDateAndModifiedMember(result , diagnosis);

        responseDTO.setDiagnosisDTO(diagnosis);

        return responseDTO;
    }

    @Transactional
    @Override
    public ResponseDTO createDiagnosis(DiagnosisDTO diagnosisDTO) {
        log.info("createDiagnosis 실행");

        // 마지막 id 가져오기
        int lastDiagnosisId = diagnosisRepository.getLastDiagnosisId();
        // 마지막 id + 1 값 대입
        diagnosisDTO.setDiagnosisId(lastDiagnosisId + 1 );
//        DiagnosisVO result =  diagnosisRepository.createDiagnosis(diagnosisDTO);


        return null;
    }


}
