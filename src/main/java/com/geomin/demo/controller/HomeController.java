package com.geomin.demo.controller;

import com.geomin.demo.dto.PatientDTO;
import com.geomin.demo.dto.UserSecurityDTO;
import com.geomin.demo.dto.VitalsDTO;
import com.geomin.demo.service.PatientService;
import com.geomin.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final PatientService patientService;

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

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody UserDTO userDTO){
//
//        log.info("post >> /login... login() 실행됨.");
//        log.info("userDTO::{}",userDTO);
//
//        UserVO userVO = userService.login(userDTO);
//
//        if(userVO == null){
//            log.info("실패");
//            log.info("userVO::{}" , userVO);
//
//            String error = "로그인에 실패하였습니다.";
//
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
//        }
//
//        return ResponseEntity.status(HttpStatus.OK).body(userVO);
//    }

    // 접수 페이지
//    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/reception")
    public String getHome(Principal principal , Model model){

        log.info("get >> /reception... getHome() 실행됨.");

        log.info(principal.getName());
        UserSecurityDTO user = userService.getUser(principal.getName());

        model.addAttribute("user" , user);


        return "reception";
    }

    // 환자 검색
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



}
