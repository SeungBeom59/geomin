package com.geomin.demo.controller;

import com.geomin.demo.dto.*;
import com.geomin.demo.resolver.CommonModel;
import com.geomin.demo.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final PatientService patientService;
    private final WaitingService waitingService;
    private final DiagnosisService diagnosisService;
    private final KcdService kcdService;
    private final FileService fileService;
    private final MedicineService medicineService;
    private final TreatmentService treatmentService;
    private final MedicalMaterialService medicalMaterialService;
    private final ServiceFacade serviceFacade;

    // 로그인
    @GetMapping("/login")
    public String getLogin(String logout) {

        log.info("logout: " + logout);      // 사용자 logout 요청시에는 null이 아닌 "" 빈 값 들어오게 됨.

        if(logout != null){
            log.info("user logout......");
        }

        return "login";
    }

    // 접수 페이지
//    @PreAuthorize("hasAnyRole('USER')")
    @CommonModel
    @GetMapping(value = {"/reception" , "/" , ""})
    public String getHome(Model model, @PageableDefault(size = 4) Pageable pageable){

        return "pages/reception";
    }

    // 수납 페이지
    @GetMapping("pay")
    @CommonModel
    public String getPay(Model model, @PageableDefault(size = 4) Pageable pageable){


        return "pages/pay";
    }


    // 환자 검색 팝업창
    @GetMapping("/patient-search/{name}")
    public String getPatientList( @PathVariable(name = "name") String patientName ,
                                  @PageableDefault(size = 5) Pageable pageable ,
                                  PatientDTO patientDTO,
                                  Model model
    ){


        patientDTO.setPatientName(patientName);

        Page<PatientDTO> patients =  patientService.getPatientList(patientDTO , pageable);

        model.addAttribute("searchValue" , patientName);
        model.addAttribute("patients" , patients);

//        log.info("patients::{}", patients.stream().toList());

        return "patient_popup";
    }


    // 진료 접수 팝업창
    @GetMapping("/waiting/{patientId}")
    public String getWaiting(@PathVariable("patientId") int patientId , Model model){

//        PatientDTO patientDTO = new PatientDTO();
//        patientDTO.setPatientId(Integer.parseInt(patientId));

        PatientDTO dto = patientService.getPatientById(patientId);
//        PatientDTO dto = patientService.getPatientById(patientDTO);
        dto.setIdentify(WaitingUtil.getIdentify(dto.getIdentify()));
//        log.info("dto::{}",dto);

        model.addAttribute("patient" , dto);

        return "/waiting_popup";
    }



    // 진료 접수 추가
    @PostMapping("/waiting/post")
    public ResponseEntity<?> addWaiting(
            @RequestBody WaitingDTO waitingDTO,
            Principal principal ,
            @PageableDefault(size = 4) Pageable pageable){



//        log.info("waitingDTO::{}",waitingDTO);

        int result = waitingService.addWaiting(waitingDTO , principal);

        if(result == 2){
            UserSecurityDTO user = userService.getUser(principal.getName());
            Page<WaitingDTO> waitingList = waitingService.getWaitingList(pageable , user.getDepartmentId());

            return ResponseEntity.status(HttpStatus.OK).body(waitingList);
        }
        else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("실패");
        }
    }


    // 진료 접수 리스트 가져오기
    @PostMapping("/waiting")
    public ResponseEntity<?> searchWaiting(
            @RequestBody Map<String, Integer> requestBody,
            @PageableDefault(size = 4) Pageable pageable,
            Principal principal){

        int page = requestBody.get("page") != null? requestBody.get("page") : 0;

//        log.info("page = " + page);

        UserSecurityDTO user = userService.getUser(principal.getName());

        Page<WaitingDTO> waitingList = waitingService.getWaitingList(pageable.withPage(page) , user.getDepartmentId());
        log.info("waitingList::{}" , waitingList.stream().toList());

        return ResponseEntity.status(HttpStatus.OK).body(waitingList);
    }

    // 진료 완료 접수 환자 리스트 가져오기 (사이드바)
    @PostMapping("/waiting/end/{page}")
    public ResponseEntity<?> endWaiting(@PathVariable(name = "page" , required = false) int page,
                                        Principal principal){

        UserSecurityDTO user = userService.getUser(principal.getName());
        int departmentId = user.getDepartmentId();

        ResponseDTO response = waitingService.getEndWaitingList(page , departmentId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // 진료접수 상태||종류 변경
    @PostMapping("/waiting-modify")
    public ResponseEntity<?> modifyWaitingStatus(@RequestBody WaitingDTO waitingDTO){


//        log.info("waitingDTO::{}" , waitingDTO);

        int result = waitingService.modifyWaitingStatus(waitingDTO);

        if(result == 1){
            return ResponseEntity.status(HttpStatus.OK).body("성공");
        }
        else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("실패");
        }
    }

    // 진료완료 수 가져오기
    @PostMapping("/waiting-end-cnt")
    public ResponseEntity<?> getWaitingEndCnt(@RequestBody WaitingDTO waitingDTO){



//        log.info("waitingDTO::{}", waitingDTO);
        int waitingEndCnt = waitingService.getEndCount(waitingDTO.getDepartmentId());

        waitingDTO.setWaitingEndCnt(waitingEndCnt);
//        log.info("waitingDTO::{}" , waitingDTO);

        return ResponseEntity.status(HttpStatus.OK).body(waitingDTO);
    }

    // 환자 등록
    @PostMapping("/patient-add")
    public ResponseEntity<?> addPatient(@RequestBody PatientDTO patientDTO){



//        log.info("patientDTO::{}",patientDTO);

         int result = patientService.addPatient(patientDTO);

         if(result == 0){
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("서버 또는 사용자 에러");
         }
         else {
             PatientDTO dto = patientService.getPatient(patientDTO);

             return ResponseEntity.status(HttpStatus.OK).body(dto);
         }

    }

    // 환자 정보 업데이트
    @PostMapping("/patient-update")
    public ResponseEntity<?> updatePatient(@RequestBody PatientDTO patientDTO){


//        log.info("patientDTO::{}" , patientDTO);

        int result = patientService.updatePatient(patientDTO);

        if(result == 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("서버 또는 사용자 에러");
        }
        else{
            PatientDTO dto = patientService.getPatientById(patientDTO.getPatientId());

            if(patientDTO.getPatientId() == 0){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("서버 또는 사용자 에러");
            }
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        }
    }

    // 환자 정보 가져오기
    @PostMapping("/patient-get")
    public ResponseEntity<?> getPatient(@RequestBody PatientDTO patientDTO){

//        log.info("patientDTO::{}" , patientDTO);

        PatientDTO dto = patientService.getPatientById(patientDTO.getPatientId());

        if(dto.getPatientId() == 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("서버 또는 사용자 에러");
        }
        else {
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        }
    }

    // 활력징후 가져오기
    @PostMapping("/vitals-search")
    public ResponseEntity<Page<VitalsDTO>> getVitalsList(
            @PageableDefault(size = 1 , sort = "vital_date" , direction = Sort.Direction.DESC) Pageable pageable ,
            @RequestBody VitalsDTO vitalsDTO){


//        log.info("vitalsDTO::{}", vitalsDTO);

        Page<VitalsDTO> vitals = patientService.getVitalsList(vitalsDTO , pageable);

        return ResponseEntity.status(HttpStatus.OK).body(vitals);
    }

    // 활력징후 등록
    @PostMapping("/vitals-add")
    public ResponseEntity<?> addVitals(
        @PageableDefault(size = 1 , sort = "vital_date" , direction = Sort.Direction.DESC) Pageable pageable,
        @RequestBody VitalsDTO vitalsDTO ){


//        log.info("vitalsDTO::{}",vitalsDTO);

        int result = patientService.addVitals(vitalsDTO);

        if(result == 0){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 또는 사용자 에러");
        }
        else {
            Page<VitalsDTO> vitals = patientService.getVitalsList(vitalsDTO , pageable);

            return ResponseEntity.status(HttpStatus.OK).body(vitals);
        }

    }

    // 활력징후 업데이트
    @PostMapping("/vitals-update")
    public ResponseEntity<?> updateVitals(
            @PageableDefault(size = 1 , sort = "vital_date" , direction = Sort.Direction.DESC) Pageable pageable,
            @RequestBody VitalsDTO vitalsDTO){

//        log.info("vitalsDTO::{}",vitalsDTO);

        int result = patientService.updateVitals(vitalsDTO);

        if(result == 0){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 또는 사용자 에러");
        }
        else {
            Page<VitalsDTO> vitals = patientService.getVitalsList(vitalsDTO , pageable);

            return ResponseEntity.status(HttpStatus.OK).body(vitals);
        }

    }


    @PostMapping("/diagnosis")
    public ResponseEntity<?> getDiagnosisList(@RequestParam(name = "page" , required = false) int page,
                                                       @RequestParam(name = "sort" , defaultValue = "true") boolean isSorted,
                                                       @RequestBody DiagnosisDTO diagnosisDTO ){

        log.info("post >>> /diagnosis?page=" + page + "&sort=" + isSorted + " getDiagnosisList 실행됨");
//        log.info("diagnosisDTO::{}",diagnosisDTO);
//        log.info("isSorted:{}",isSorted);

        diagnosisDTO.setSort(isSorted);
//        log.info("diagnosisDTO::{}",diagnosisDTO);

        ResponseDTO response = diagnosisService.getDiagnosisList(page , diagnosisDTO);

        log.info("response::{}",response);

        if(response == null){
            log.info("response = null : 환자 과거 진료기록 없음..");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("데이터 정보 없음");
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PostMapping("/diagnosis/{diagnosisId}")
    public ResponseEntity<?> getDiagnosisById(@PathVariable(name = "diagnosisId") int diagnosisId){



        ResponseDTO response = diagnosisService.getDiagnosisById(diagnosisId);

        log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        log.info("response::{}",response);

        if(response == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("데이터 정보 없음");
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // 진료대기환자 호출
    @Transactional
    @PostMapping("/patient-call")
    public ResponseEntity<?> callPatient(@RequestBody WaitingDTO waitingDTO){


//        log.info("waitingDTO::{}" , waitingDTO);

        // action 없이 waiting-modify에 waitingId만 요청 받을 경우, 호출 버튼을 누른 것이므로
        // action을 "진료중"으로 바꾸어준다.
        if(waitingDTO.getAction() == null){
            waitingDTO.setAction("진료중");
        }

        int result = waitingService.modifyWaitingStatus(waitingDTO);    // waiting status 변경(진료중)

        DiagnosisDTO todayDiagnosisDTO = diagnosisService.getTodayDiagnosis(waitingDTO.getWaitingId());

//        PatientDTO patient = new PatientDTO();
//        patient.setPatientId(todayDiagnosisDTO.getPatientId());
        PatientDTO patientDTO = patientService.getPatientById(todayDiagnosisDTO.getPatientId());

        ResponseDTO callResponse = ResponseDTO.builder()
                .diagnosisDTO(todayDiagnosisDTO)
                .patientDTO(patientDTO)
                .build();

        if(result != 1 || callResponse.getPatientDTO() == null || callResponse.getDiagnosisDTO() == null){
            return ResponseEntity.internalServerError().body("서버 요청 처리 실패");
        }

        return ResponseEntity.status(HttpStatus.OK).body(callResponse);
    }

    // 진료 기록 작성 | 수정
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/diagnosis-update")
    @Transactional
    public ResponseEntity<?> updateDiagnosis(
            @RequestPart("diagnosis") DiagnosisDTO diagnosisDTO ,
            @RequestPart(value = "uploadFiles" , required = false) List<MultipartFile> uploadFiles,
            @RequestPart(value = "pills" , required = false) List<MedicineDTO> pills,
            @RequestPart(value = "deleteFiles" , required = false) List<Integer> deleteFiles,
            @RequestPart(value = "kcds" , required = false) List<KcdDTO> kcds ,
            @RequestPart(value = "treatments" , required = false) List<TreatmentDTO> treatments ,
            @RequestPart(value = "medicals" , required = false) List<MedicalMaterialDTO> medicals,
            Principal principal){


        log.info("diagnosisDTO::{}" , diagnosisDTO);
        log.info("--------------------------------------------------------------------------");
        log.info("uploadFiles::{}" , uploadFiles);
        log.info("pills::{}" , pills);
        log.info("deleteFiles::{}" , deleteFiles);
        log.info("kcds::{}" , kcds);
        log.info("medicals::{}" , medicals);
        log.info("treatments::{}" , treatments);

        UserSecurityDTO user = userService.getUser(principal.getName());    // 신원확인 정보 가져오기
        ResponseDTO result = null;

        // todo: 여기서 진료접수 이후 초회 진료기록 작성과 수정절차에 대한 분기처리 필요,
        //  - 초회 진료기록 작성인지 판별하고 초회기록 작성(update)처리 필요.
        //  - 2차 진료기록 수정이라면, 수납처리 중인지 또는 수납처리가 완료된 것인지 확인을 위해 isLock을 살펴보고
        //  - 수정처리  진행가능토록해야함.

        result = serviceFacade.updateProcessDiagnosis(diagnosisDTO ,
                uploadFiles ,deleteFiles , pills , kcds ,treatments , medicals , principal);

        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    // 진료기록 삭제 요청처리 (db에서 논리삭제, 실제 삭제 아님.)
    @PostMapping("/diagnosis-delete/{diagnosisId}")
    public ResponseEntity<?> deleteDiagnosis(@PathVariable(name = "diagnosisId") int diagnosisId){

        boolean result = diagnosisService.deleteDiagnosisById(diagnosisId);

        if(result){
            return ResponseEntity.status(HttpStatus.OK).body("진료기록 삭제 완료");
        }
        else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("진료기록 삭제 실패");
        }
    }

    // 파일 미리보기
    @GetMapping("/files/{fileName}")
    public ResponseEntity<?> viewFile(@PathVariable String fileName){


        return fileService.viewFile(fileName);
    }

    // 파일 다운로드
    @GetMapping("/files/download/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName){



        return fileService.downloadFile(fileName);
    }

    // 파일 삭제
//    @PostMapping("/files/delete")
    public ResponseEntity<?> deleteFile(@RequestBody FileInfoDTO fileInfoDTO){



        boolean isOk = fileService.deleteFile(fileInfoDTO);

        if(isOk){
            return ResponseEntity.status(HttpStatus.OK).body("파일 처리 성공");
        }
        else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 처리 실패.");
        }
    }





}
