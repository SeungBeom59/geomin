package com.geomin.demo.service;

import com.geomin.demo.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServiceFacade {

    private final UserService userService;
    private final DiagnosisService diagnosisService;
    private final KcdService kcdService;
    private final FileService fileService;
    private final MedicineService medicineService;
    private final TreatmentService treatmentService;
    private final MedicalMaterialService medicalMaterialService;
    private final DiagnosisBillService diagnosisBillService;

    // 진료기록의 초회 판별하여 적절하게 update 진행
    public ResponseDTO updateProcessDiagnosis(DiagnosisDTO diagnosisDTO ,
                                              List<MultipartFile> uploadFiles ,
                                              List<Integer> deleteFiles,
                                              List<MedicineDTO> pills,
                                              List<KcdDTO> kcds ,
                                              List<TreatmentDTO> treatments ,
                                              List<MedicalMaterialDTO> medicals ,
                                              Principal principal){

        ResponseDTO result = null;

        // 초회 진료기록 작성인지 판별하여 메소드 분기처리
        if(diagnosisDTO.getDiagnosisYn() == null || diagnosisDTO.getDiagnosisYn() == false){
            result = firstUpdateDiagnosis(diagnosisDTO ,
                    uploadFiles ,deleteFiles , pills , kcds ,treatments , medicals , principal);

            // todo: 초회 진료기록 작성 이후 수납내역도 만들도록
            int isGood = diagnosisBillService.createDiagnosisBill(result.getDiagnosisDTO());
        }
        // islock 잠궜는지 확인하고 업데이트 처리 필요
        else{
            result = updateDiagnosis(diagnosisDTO ,
                    uploadFiles ,deleteFiles , pills , kcds ,treatments , medicals , principal);
        }

        return result;
    }


    // 초회 진료기록 작성
    public ResponseDTO firstUpdateDiagnosis(DiagnosisDTO diagnosisDTO ,
                                            List<MultipartFile> uploadFiles ,
                                            List<Integer> deleteFiles,
                                            List<MedicineDTO> pills,
                                            List<KcdDTO> kcds ,
                                            List<TreatmentDTO> treatments ,
                                            List<MedicalMaterialDTO> medicals ,
                                            Principal principal){

        UserSecurityDTO user = userService.getUser(principal.getName());    // 신원확인 정보 가져오기

        if(kcds != null){
            int kcdId = kcdService.addKcd(kcds);
            diagnosisDTO.setKcdId(kcdId);
        }

        if(uploadFiles != null){
            int fileId = fileService.upload(uploadFiles);
            diagnosisDTO.setFileId(fileId);
        }

        if(pills != null){
            int medicineId = medicineService.addMedicine(pills);
            diagnosisDTO.setMedicineId(medicineId);
        }

        if(treatments != null){
            int treatmentId = treatmentService.insertTreatment(treatments);
            diagnosisDTO.setTreatmentId(treatmentId);
        }

        if(medicals != null){
            int medicalBillId = medicalMaterialService.insertMedicalBills(medicals);
            diagnosisDTO.setMedicalBillId(medicalBillId);
        }

        diagnosisDTO.setDoctorId(user.getReferenceId());


        return diagnosisService.updateDiagnosisById(diagnosisDTO);
    }

    // 작성되었던 진료기록을 수정하는 경우(초회X)
    public ResponseDTO updateDiagnosis(DiagnosisDTO diagnosisDTO ,
                                       List<MultipartFile> uploadFiles ,
                                       List<Integer> deleteFiles,
                                       List<MedicineDTO> pills,
                                       List<KcdDTO> kcds ,
                                       List<TreatmentDTO> treatments ,
                                       List<MedicalMaterialDTO> medicals ,
                                       Principal principal){

        UserSecurityDTO user = userService.getUser(principal.getName());    // 신원확인 정보 가져오기


        // kcdId, 이미 질병기록이 존재하는가? (수정)
        if(diagnosisDTO.getKcdId() > 0){
            // 이미 있던 기존의 질병기록 코드에 대한 수정
            int kcdId = kcdService.deleteAndCreateKcds(diagnosisDTO.getKcdId() , kcds);
            diagnosisDTO.setKcdId(kcdId);
        }

        // 기존에 업로드된 파일에 대하여, 삭제요청이 있을 경우.
        if(deleteFiles != null){
            int fileId = fileService.deleteFiles(diagnosisDTO.getFileId() , deleteFiles);
            diagnosisDTO.setFileId(fileId);
        }

        // 파일이 존재 한다면 업로드 진행, 파일 저장 id 대입
        if( uploadFiles != null){
            if(diagnosisDTO.getFileId() > 0 ){
                log.info("fileInfoId = " + diagnosisDTO.getFileId());
                int fileId = fileService.uploadAdditionalFiles(uploadFiles, diagnosisDTO.getFileId());
                diagnosisDTO.setFileId(fileId);
            }
            else {
                int fileId = fileService.upload(uploadFiles);
                diagnosisDTO.setFileId(fileId);
            }
        }


        // 기존의 medicineId, 의약품정보가 존재하는가? (수정)
        if(diagnosisDTO.getMedicineId() > 0){
            int medicineId = medicineService.deleteAndCreateMedicine(diagnosisDTO.getMedicineId() , pills);
            diagnosisDTO.setMedicineId(medicineId);
        }

        // 기존의 treatmentId, 즉 처방수가 기록 번호가 존재하는가? (수정)
        if(diagnosisDTO.getTreatmentId() > 0){
            int treatmentId = treatmentService.deleteAndCreateTreatment(diagnosisDTO.getTreatmentId() , treatments);
            diagnosisDTO.setTreatmentId(treatmentId);
        }

        // 기존의 medicalBillId가 존재, 치료재료 기록 번호가 존재하는가? (수정)
        if(diagnosisDTO.getMedicalBillId() > 0){
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("medicalBillId = " + diagnosisDTO.getMedicalBillId());
            int medicalBillId = medicalMaterialService.deleteAndCreateMedicalBill(diagnosisDTO.getMedicalBillId() , medicals);
            diagnosisDTO.setMedicalBillId(medicalBillId);
        }

        // 진료했던 진료기록이라면 수정자에 아이디 넣기
        diagnosisDTO.setDiagnosisModifier(user.getReferenceId());

        return diagnosisService.updateDiagnosisById(diagnosisDTO);
    }



//    // 진료기록 분기처리에 의한 작성 또는 수정
//    public ResponseDTO createOrModifyDiagnosis(DiagnosisDTO diagnosisDTO ,
//                                               List<MultipartFile> uploadFiles ,
//                                               List<Integer> deleteFiles,
//                                               List<MedicineDTO> pills,
//                                               List<KcdDTO> kcds ,
//                                               List<TreatmentDTO> treatments ,
//                                               List<MedicalMaterialDTO> medicals ,
//                                               Principal principal){
//
//
//
//        UserSecurityDTO user = userService.getUser(principal.getName());    // 신원확인 정보 가져오기
//
//        // kcdId, 질병기록이 존재하지 않고  저장시킬 kcd가 존재하는가? (신규)
//        if(diagnosisDTO.getKcdId() <= 0 && kcds != null){
//            int kcdId = kcdService.addKcd(kcds);
//            diagnosisDTO.setKcdId(kcdId);
//        }
//        // kcdId, 이미 질병기록이 존재하는가? (수정)
//        else if(diagnosisDTO.getKcdId() > 0){
//            // 이미 있던 기존의 질병기록 코드에 대한 수정
//            int kcdId = kcdService.deleteAndCreateKcds(diagnosisDTO.getKcdId() , kcds);
//            diagnosisDTO.setKcdId(kcdId);
//        }
//
//        // 기존에 업로드된 파일에 대하여, 삭제요청이 있을 경우.
//        if(deleteFiles != null){
//            int fileId = fileService.deleteFiles(diagnosisDTO.getFileId() , deleteFiles);
//            diagnosisDTO.setFileId(fileId);
//        }
//
//        // 파일이 존재 한다면 업로드 진행, 파일 저장 id 대입
//        if( uploadFiles != null){
//            if(diagnosisDTO.getFileId() > 0 ){
//                log.info("fileInfoId = " + diagnosisDTO.getFileId());
//                int fileId = fileService.uploadAdditionalFiles(uploadFiles, diagnosisDTO.getFileId());
//                diagnosisDTO.setFileId(fileId);
//            }
//            else {
//                int fileId = fileService.upload(uploadFiles);
//                diagnosisDTO.setFileId(fileId);
//            }
//        }
//
//        // medicineId가 존재하지 않고 저장시킬 의약품 정보는 있는가? (신규)
//        if(diagnosisDTO.getMedicineId() <= 0 && pills != null){
//            int medicineId = medicineService.addMedicine(pills);
//            diagnosisDTO.setMedicineId(medicineId);
//        }
//        // 기존의 medicineId, 의약품정보가 존재하는가? (수정)
//        else if(diagnosisDTO.getMedicineId() > 0){
//            int medicineId = medicineService.deleteAndCreateMedicine(diagnosisDTO.getMedicineId() , pills);
//            diagnosisDTO.setMedicineId(medicineId);
//        }
//
//        // treatmentId가 존재하지 않고 저장시킬 치료수가 정보가 있는가(신규)
//        if(diagnosisDTO.getTreatmentId() <= 0 && treatments != null){
//            int treatmentId = treatmentService.insertTreatment(treatments);
//            diagnosisDTO.setTreatmentId(treatmentId);
//        }
//        // 기존의 treatmentId, 즉 처방수가 기록 번호가 존재하는가? (수정)
//        else if(diagnosisDTO.getTreatmentId() > 0){
//            int treatmentId = treatmentService.deleteAndCreateTreatment(diagnosisDTO.getTreatmentId() , treatments);
//            diagnosisDTO.setTreatmentId(treatmentId);
//        }
//
//        // medicalBillId가 존재하지 않고 저장시킬 치료재료 정보가 있는가?(신규)
//        if(diagnosisDTO.getMedicalBillId() <= 0 && medicals != null){
//            int medicalBillId = medicalMaterialService.insertMedicalBills(medicals);
//            diagnosisDTO.setMedicalBillId(medicalBillId);
//        }
//        // 기존의 medicalBillId가 존재, 치료재료 기록 번호가 존재하는가? (수정)
//        else if(diagnosisDTO.getMedicalBillId() > 0){
//            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//            log.info("medicalBillId = " + diagnosisDTO.getMedicalBillId());
//            int medicalBillId = medicalMaterialService.deleteAndCreateMedicalBill(diagnosisDTO.getMedicalBillId() , medicals);
//            diagnosisDTO.setMedicalBillId(medicalBillId);
//        }
//
//        // 진료했던 진료기록이라면 수정자에 아이디 넣기
//        if(diagnosisDTO.getDiagnosisYn() != null && diagnosisDTO.getDiagnosisYn()){
//            diagnosisDTO.setDiagnosisModifier(user.getReferenceId());
//        }
//        // 아니라면 담당의에 의사 id 설정
//        else {
//            diagnosisDTO.setDoctorId(user.getReferenceId());
//        }
//
//        log.info("업데이트 전 마지막 dto 형태 ::{}" , diagnosisDTO);
//        // id가 존재(기존 진료기록 또는 진료접수를 통한 진료기록)
//        if(diagnosisDTO.getDiagnosisId() > 0){
//            result =  diagnosisService.updateDiagnosisById(diagnosisDTO);
////            log.info("result::{}", result);
//        }
//        // id가 0 또는 그 이하인 경우, 진료접수 없이 작성한 새로운 진료기록
//        else {
//            log.info("새로운 진료기록 작성 서비스로 넘어갑니다.");
////            log.info("diagnosisDTO::{}" , diagnosisDTO);
//            result = diagnosisService.createDiagnosis(diagnosisDTO);
//        }
//
//        return result;
//    }


}
