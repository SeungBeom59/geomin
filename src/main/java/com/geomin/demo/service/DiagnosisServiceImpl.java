package com.geomin.demo.service;

import com.geomin.demo.domain.*;
import com.geomin.demo.dto.*;
import com.geomin.demo.repository.DiagnosisRepository;
import com.geomin.demo.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final KcdService kcdService;
    private final WaitingService waitingService;
    private final DoctorRepository doctorRepository;
    private final TreatmentService treatmentService;
    private final MedicalMaterialService medicalMaterialService;

//    @Override
//    public Page<DiagnosisDTO> getDiagnosisList(Pageable pageable , DiagnosisDTO diagnosisDTO) {
//
//        Sort sort = diagnosisDTO.isSort() ?
//                Sort.by(Sort.Order.desc("diagnosis_date")) : Sort.by(Sort.Order.asc("diagnosis_date"));
//
//        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
//
//        List<DiagnosisVO> result = diagnosisRepository.getDiagnosisList(pageable , diagnosisDTO);
//
//        log.info("result::{}",result);
//
//        List<DiagnosisDTO> content = new ArrayList<>();
//
//        for(DiagnosisVO vo : result){
//            DiagnosisDTO dto = new DiagnosisDTO();
//
//            PatientVO patient = vo.getPatient();
//            DoctorVO doctor = vo.getDoctor();
//            DepartmentVO department = vo.getDepartment();
//
//            dto = vo.getDateAndModifiedMember(vo, dto);
//            dto.setDiagnosisId(vo.getDiagnosisId());
//            dto.setPatientId(patient.getPatientId());
//            dto.setPatientName(patient.getPatientName());
//            dto.setDoctorId(doctor.getDoctorId());
//            dto.setDoctorName(doctor.getDoctorName());
//            dto.setDepartmentId(department.getDepartmentId());
//            dto.setDepartmentName(department.getDepartmentName());
//            dto.setSymptoms(vo.getSymptoms());
//            dto.setDiagnosis(vo.getDiagnosis());
//            dto.setPrescription(vo.getPrescription());
//
//            dto.setFileId(vo.getFileId());
//
//            content.add(dto);
//        }
//
//        log.info("content::{}", content);
//
//        int total = diagnosisRepository.getTotalDiagnosis(diagnosisDTO);
//
//        return new PageImpl<>(content, pageable, total);
//    }

    @Override
    public ResponseDTO getDiagnosisById(int diagnosisId) {

        DiagnosisVO diagnosisVO = diagnosisRepository.getDiagnosisById(diagnosisId);

        if(diagnosisVO == null) {
            return null;
        }

        ResponseDTO responseDTO = createResponseDTO(diagnosisVO);

        return responseDTO;
    }

    @Override
    public boolean deleteDiagnosisById(int diagnosisId) {

      diagnosisRepository.deleteDiagnosisById(diagnosisId);

        DiagnosisVO diagnosisVO = diagnosisRepository.getDiagnosisById(diagnosisId);

        if(diagnosisVO != null ) {
            if(diagnosisVO.getDiagnosisId() == diagnosisId && diagnosisVO.getDiagnosisDelYn()) {
                return true;
            }
            else {
                return false;
            }
        }
        else{
            return false;
        }
    }


    @Override
    public ResponseDTO getDiagnosisList(int page, DiagnosisDTO diagnosisDTO) {

        int size = 15;
        int offset = page * size;

        List<DiagnosisVO> result = diagnosisRepository.getDiagnosisList(diagnosisDTO , page, size , offset);

        if(result.isEmpty()){
            return null;
        }

        List<DiagnosisDTO> dtos = new ArrayList<>();

        result.forEach(vo -> {

            DiagnosisDTO dto = DiagnosisDTO.builder()
                    .diagnosisId(vo.getDiagnosisId())
                    .patientId(vo.getPatient().getPatientId())
                    .patientName(vo.getPatient().getPatientName())
                    .doctorId(vo.getDoctor().getDoctorId())
                    .doctorName(vo.getDoctor().getDoctorName())
                    .departmentId(vo.getDepartment().getDepartmentId())
                    .departmentName(vo.getDepartment().getDepartmentName())
                    .diagnosisYn(vo.getDiagnosisYn())
                    .symptoms(vo.getSymptoms())
                    .diagnosis(vo.getDiagnosis())
                    .prescription(vo.getPrescription())
                    .fileId(vo.getFileId())
                    .medicineId(vo.getMedicineId())
                    .diagnosisDelYn(vo.getDiagnosisDelYn())
                    .kcdId(vo.getKcdId())
                    .medicalBillId(vo.getMedicalBillId())
                    .treatmentId(vo.getTreatmentId())
                    .build();

            dto = vo.getDateAndModifiedMember(vo , dto);

            dtos.add(dto);
        });

        int totalCount = diagnosisRepository.getTotalDiagnosis(diagnosisDTO);

        ResponseDTO responseDTO = createResponseDTO(result.get(0));
        responseDTO.setDiagnosisDTOList(dtos);

        int totalPages = (int)Math.ceil((double) totalCount / size );
        int currentGroup = (int)Math.ceil((double) (page + 1) / size);    // 현재 페이지가 속하는 페이지 그룹

        int currentGroupStartPage = (currentGroup - 1) * size + 1;
        int currentGroupEndPage = Math.min(currentGroup * size, totalPages);

        int prevGroupStartPage = (currentGroup - 2) * size + 1;
        prevGroupStartPage = Math.max(prevGroupStartPage, 1);       // 1 보다는 작아지지 않도록
        int nextGroupStartPage = currentGroup * size + 1;
        nextGroupStartPage = Math.min(nextGroupStartPage, totalPages);  // 전체 페이지 보다는 많아지지 않도록

        responseDTO.setPage(page);
        responseDTO.setSize(size);
        responseDTO.setTotalCount(totalCount);
        responseDTO.setTotalPages(totalPages);
        responseDTO.setCurrentGroup(currentGroup);
        responseDTO.setCurrentGroupStartPage(currentGroupStartPage);
        responseDTO.setCurrentGroupEndPage(currentGroupEndPage);
        responseDTO.setPrevGroupStartPage(prevGroupStartPage);
        responseDTO.setNextGroupStartPage(nextGroupStartPage);

        return responseDTO;
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

        return createResponseDTO(result);
    }

    @Transactional
    @Override
    public ResponseDTO createDiagnosis(DiagnosisDTO diagnosisDTO) {
        log.info("createDiagnosis 실행");

        // 마지막 id 가져오기
        int lastDiagnosisId = diagnosisRepository.getLastDiagnosisId();
        // 마지막 id + 1 값 대입
        diagnosisDTO.setDiagnosisId(lastDiagnosisId + 1 );

        // 소속정보 가져오기
        DoctorVO doc = doctorRepository.findById(diagnosisDTO.getDoctorId());
        diagnosisDTO.setDepartmentId(doc.getDepartment().getDepartmentId());

        // 새로운 diagnosis 기록 저장
        int isGood =  diagnosisRepository.createDiagnosis(diagnosisDTO);
        // 기록 저장 실패한  경우
        if(isGood <= 0){
            return null;
        }

        // id 값으로 사용했던 lastDiagnosisId +1 값으로 정보 가져오기
        DiagnosisVO result = diagnosisRepository.getDiagnosisById(lastDiagnosisId + 1);

        return createResponseDTO(result);
    }




    // 중복되는 코드가 너무 길어서 함수로 사용.
    // 진료기록 정보를 이용하여 파일, 의약품, 진료기록이 모두 감싸진 responseDTO를 만들도록.
    // + 질병기록 (24/09/06)
    // + 처방수가 , 치료재료 (24/11/07)
    public ResponseDTO createResponseDTO(DiagnosisVO result){

        log.info("result::{}" , result);

        // 파일정보, 의약품 처방 정보, 진료기록 정보를 모두 감싸는 responseDTO 생성
        // + 질병기록
        ResponseDTO responseDTO = new ResponseDTO();

        // 해당 진료기록의 파일 정보 id가 있다면 따로 가져와서 responseDTO에 넣어준다.
        // 외래키로 사용은 하나 관계는 맺지 않았음. join을 해서 가져오자니,
        // 파일정보가 몇개의 list 형태일지 모르겠다는 점과 더불어, 검색된 파일정보의 수 만큼 진료기록의 결과가 곱해져서 나오게 됨.
        // 아래 의약품도 이와 마찬가지임. + 질병기록
        if(result != null && result.getFileId() > 0){
            List<FileInfoDTO> fileList = fileService.getFileById(result.getFileId());
            responseDTO.setFileInfoDTOList(fileList);
        }

        if(result != null && result.getMedicineId() > 0){
            List<MedicineDTO> medicines = medicineService.getMedicineListById(result.getMedicineId());
            responseDTO.setMedicineDTOList(medicines);
        }

        if(result != null && result.getKcdId() > 0){
            List<KcdDTO> kcds = kcdService.getKcdListById(result.getKcdId());
            responseDTO.setKcdDTOList(kcds);
        }
        /*
            여기부분 처리해야 함. 앞단으로 넘겨서 클라이언트 화면에 뿌려줄 수 있도록 데이터 찾아와서 넣어주기
            처방수가의 비급여와 치료재료의 상한금액이 없을 경우 처리를 어떻게 할 것이냐...?
         */
        if(result != null && result.getTreatmentId() > 0){
            List<TreatmentDTO> treatments = treatmentService.getTreatmentListById(result.getTreatmentId());
            responseDTO.setTreatmentDTOList(treatments);
        }
        if(result != null && result.getMedicalBillId() > 0){
            List<MedicalMaterialDTO> medicalMaterialDTOS = medicalMaterialService.getMaterialByBillId(result.getMedicalBillId());
            responseDTO.setMedicalMaterialDTOList(medicalMaterialDTOS);
        }

        PatientVO patient = result.getPatient();
        DepartmentVO department = result.getDepartment();
        WaitingVO waiting = result.getWaiting();
        DoctorVO doctor = result.getDoctor();

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
                .medicineId(result.getMedicineId())
                .kcdId(result.getKcdId())
                .medicalBillId(result.getMedicalBillId())
                .treatmentId(result.getTreatmentId())
                .build();

        diagnosis = result.getDateAndModifiedMember(result , diagnosis);

        responseDTO.setDiagnosisDTO(diagnosis);

        return responseDTO;
    }


}
