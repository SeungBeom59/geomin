package com.geomin.demo.controller;

import com.geomin.demo.domain.DiagnosisVO;
import com.geomin.demo.domain.WaitingVO;
import com.geomin.demo.dto.*;
import com.geomin.demo.repository.DiagnosisRepository;
import com.geomin.demo.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
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

import static com.geomin.demo.domain.UserRole.ROLE_ADMIN;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final PatientService patientService;
    private final WaitingService waitingService;
    private final DiagnosisService diagnosisService;
    private final FileService fileService;

    // 로그인
    @GetMapping("/login")
    public String getLogin(String logout) {

        log.info("get >> /login... getLogin() 실행됨.");
        log.info("logout: " + logout);      // 사용자 logout 요청시에는 null이 아닌 "" 빈 값 들어오게 됨.

        if(logout != null){
            log.info("user logout......");
        }

        return "login";
    }

    // 접수 페이지
//    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping(value = {"/reception" , "/" , ""})
    public String getHome(
            Principal principal , Model model,
            @PageableDefault(size = 4) Pageable pageable
    ){
        log.info("get >> /reception... getHome() 실행됨.");
//        log.info("principal::{}",principal);
//        log.info(principal.getName());
        UserSecurityDTO user = userService.getUser(principal.getName());
//        log.info("user::{}" , user);

        Page<WaitingDTO> waitingList = waitingService.getWaitingList(pageable , user.getDepartmentId());
        log.info("page" + waitingList.getPageable());
        log.info("totalPage " + waitingList.getTotalPages());
        log.info("totalElements " + waitingList.getTotalElements());
        log.info("waitingList::{}" , waitingList.stream().toList());

        int waitingEnd = waitingService.getEndCount(user.getDepartmentId());

        model.addAttribute("user" , user);
        model.addAttribute("waitingList" , waitingList);
        model.addAttribute("waitingEnd" , waitingEnd);


        return "reception";
    }

    // 환자 검색 팝업창
    @GetMapping("/patient-search/{name}")
    public String getPatientList( @PathVariable(name = "name") String patientName ,
                                  @PageableDefault(size = 5) Pageable pageable ,
                                  PatientDTO patientDTO,
                                  Model model
    ){
        log.info("get >> /patient-search/" + patientName);

        patientDTO.setPatientName(patientName);

        Page<PatientDTO> patients =  patientService.getPatientList(patientDTO , pageable);

        model.addAttribute("searchValue" , patientName);
        model.addAttribute("patients" , patients);

        log.info("patients::{}", patients.stream().toList());

        return "patient_popup";
    }

    // 진료기록 가져오기
    @PostMapping("/diagnosis")
    public ResponseEntity<?> getDiagnosisList(@PageableDefault(size = 10) Pageable pageable ,
                                              @RequestBody DiagnosisDTO diagnosisDTO ){

        log.info("post >> /diagnosis... getDiagnosisList() 실행됨.");
        log.info("diagnosisDTO::{}" , diagnosisDTO);

        Page<DiagnosisDTO> diagnosisList = diagnosisService.getDiagnosisList(pageable, diagnosisDTO);

        return ResponseEntity.status(HttpStatus.OK).body(diagnosisList);

    }


    // 진료 접수 팝업창
    @GetMapping("/waiting/{patientId}")
    public String getWaiting(@PathVariable("patientId") String patientId , Model model){

        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setPatientId(Integer.parseInt(patientId));

        log.info("get >> /waiting/" + patientDTO.getPatientId() + "... getWaiting()실행됨");

        PatientDTO dto = patientService.getPatientById(patientDTO);
        dto.setIdentify(WaitingUtil.getIdentify(dto.getIdentify()));
        log.info("dto::{}",dto);

        model.addAttribute("patient" , dto);

        return "/waiting_popup";
    }

    // 진료 접수 추가
    @PostMapping("/waiting/post")
    public ResponseEntity<?> addWaiting(
            @RequestBody WaitingDTO waitingDTO,
            Principal principal ,
            @PageableDefault(size = 4) Pageable pageable){

        log.info("post >> /waiting/post... addWaiting() 실행됨.");

        log.info("waitingDTO::{}",waitingDTO);

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
        log.info("post >> /waiting... searchWaiting() 실행됨");
        log.info("page = " + page);

        UserSecurityDTO user = userService.getUser(principal.getName());

        Page<WaitingDTO> waitingList = waitingService.getWaitingList(pageable.withPage(page) , user.getDepartmentId());
        log.info("waitingList::{}" , waitingList.stream().toList());

        return ResponseEntity.status(HttpStatus.OK).body(waitingList);
    }

    // 진료접수 상태||종류 변경
    @PostMapping("/waiting-modify")
    public ResponseEntity<?> modifyWaitingStatus(@RequestBody WaitingDTO waitingDTO){

        log.info("post >> /waiting-modify... modifyWaitingStatus() 실행됨");
        log.info("waitingDTO::{}" , waitingDTO);

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

        log.info("post >> /waiting-end-cnt... getWaitingEndCnt() 실행.");

        log.info("waitingDTO::{}", waitingDTO);
        int waitingEndCnt = waitingService.getEndCount(waitingDTO.getDepartmentId());

        waitingDTO.setWaitingEndCnt(waitingEndCnt);
        log.info("waitingDTO::{}" , waitingDTO);

        return ResponseEntity.status(HttpStatus.OK).body(waitingDTO);
    }

    // 환자 등록
    @PostMapping("/patient-add")
    public ResponseEntity<?> addPatient(@RequestBody PatientDTO patientDTO){


        log.info("Post >> /patient-add... addPatient() 실행됨");
        log.info("patientDTO::{}",patientDTO);

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

        log.info("Post >> /patient-update... updatePatient() 실행됨");
        log.info("patientDTO::{}" , patientDTO);

        int result = patientService.updatePatient(patientDTO);

        if(result == 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("서버 또는 사용자 에러");
        }
        else{
            PatientDTO dto = patientService.getPatientById(patientDTO);

            if(patientDTO.getPatientId() == 0){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("서버 또는 사용자 에러");
            }
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        }
    }

    // 환자 정보 가져오기
    @PostMapping("/patient-get")
    public ResponseEntity<?> getPatient(@RequestBody PatientDTO patientDTO){

        log.info("Post >> /patient-get... getPatient() 실행됨");
        log.info("patientDTO::{}" , patientDTO);

        PatientDTO dto = patientService.getPatientById(patientDTO);

        if(dto.getPatientId() == 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("서버 또는 사용자 에러");
        }
        else {
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        }
    }

    // 진료대기환자 호출
    @Transactional
    @PostMapping("/patient-call")
    public ResponseEntity<?> callPatient(@RequestBody WaitingDTO waitingDTO){

        log.info("post >> /patient-call... callPatient() 실행.");
        log.info("waitingDTO::{}" , waitingDTO);

        // action 없이 waiting-modify에 waitingId만 요청 받을 경우, 호출 버튼을 누른 것이므로
        // action을 "진료중"으로 바꾸어준다.
        if(waitingDTO.getAction() == null){
            waitingDTO.setAction("진료중");
        }

        int result = waitingService.modifyWaitingStatus(waitingDTO);    // waiting status 변경(진료중)

        DiagnosisDTO todayDiagnosisDTO = diagnosisService.getTodayDiagnosis(waitingDTO.getWaitingId());

        PatientDTO patient = new PatientDTO();
        patient.setPatientId(todayDiagnosisDTO.getPatientId());
        PatientDTO patientDTO = patientService.getPatientById(patient);

        CallPatientDTO callResponse = new CallPatientDTO(patientDTO , todayDiagnosisDTO);

        if(result != 1 || callResponse.getPatient() == null || callResponse.getTodayDiagnosis() == null){
            return ResponseEntity.internalServerError().body("서버 요청 처리 실패");
        }

        return ResponseEntity.status(HttpStatus.OK).body(callResponse);
    }


    // 활력징후 가져오기
    @PostMapping("/vitals-search")
    public ResponseEntity<Page<VitalsDTO>> getVitalsList(
            @PageableDefault(size = 1 , sort = "vital_date" , direction = Sort.Direction.DESC) Pageable pageable ,
            @RequestBody VitalsDTO vitalsDTO){

        log.info("post >> /vitals-search... getVitalsList() 실행됨.");

        log.info("vitalsDTO::{}", vitalsDTO);

        Page<VitalsDTO> vitals = patientService.getVitalsList(vitalsDTO , pageable);

        return ResponseEntity.status(HttpStatus.OK).body(vitals);
    }

    // 활력징후 등록
    @PostMapping("/vitals-add")
    public ResponseEntity<?> addVitals(
        @PageableDefault(size = 1 , sort = "vital_date" , direction = Sort.Direction.DESC) Pageable pageable,
        @RequestBody VitalsDTO vitalsDTO ){

        log.info("post >> /vitals-add... addVitals() 실행됨.");
        log.info("vitalsDTO::{}",vitalsDTO);

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

        log.info("post >> /vitals-update... updatevitals() 실행됨.");
        log.info("vitalsDTO::{}",vitalsDTO);

        int result = patientService.updateVitals(vitalsDTO);

        if(result == 0){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 또는 사용자 에러");
        }
        else {
            Page<VitalsDTO> vitals = patientService.getVitalsList(vitalsDTO , pageable);

            return ResponseEntity.status(HttpStatus.OK).body(vitals);
        }

    }

    // 진료 기록 작성 | 수정
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/diagnosis-update")
    @Transactional
    public ResponseEntity<?> updateDiagnosis(
            @RequestPart("diagnosis") DiagnosisDTO diagnosisDTO ,
            @RequestPart("uploadFiles") List<MultipartFile> uploadFiles,
            Principal principal){

        log.info("post >> /diagnosis-update... updateDiagnosis() 실행.");
        log.info("diagnosisDTO::{}" , diagnosisDTO);
        log.info("--------------------------------------------------------------------------");
        log.info("uploadFiles::{}" , uploadFiles);

        UserSecurityDTO user = userService.getUser(principal.getName());    // 신원확인 정보 가져오기

        // 의사인지 체크
        if(user.getRoleSet() == ROLE_ADMIN){
            diagnosisDTO.setDoctorId(user.getReferenceId());    //  의사 id 설정
        }
        else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한부족으로 인한 진료기록 작성 불가");
        }

        // 진료했던 진료기록이라면 수정자에 아이디 넣기
        if(diagnosisDTO.getDiagnosisYn()){
            diagnosisDTO.setDiagnosisModifier(user.getId());
        }

        // 파일이 존재 한다면 업로드 진행, 파일 저장 id 대입
        if( !uploadFiles.isEmpty() ){
            int fileId = fileService.upload(uploadFiles);
            diagnosisDTO.setFileId(fileId);
        }

        // id가 존재(기존 진료기록 또는 진료접수를 통한 진료기록)
        if(diagnosisDTO.getDiagnosisId() > 0){
            DiagnosisDTO result =  diagnosisService.updateDiagnosisById(diagnosisDTO);
            log.info("result::{}", result);
        }
        // id가 0 또는 그 이하인 경우, 진료접수 없이 작성한 새로운 진료기록
        else {
//            diagnosisDTO.setDiagnosisYn(true);  // 진료완료 처리
//            diagnosisService.createDiagnosis();
        }

        return ResponseEntity.status(HttpStatus.OK).body("");
    }



}
