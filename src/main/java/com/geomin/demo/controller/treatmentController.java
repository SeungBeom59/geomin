package com.geomin.demo.controller;

import com.geomin.demo.dto.MedicalMaterialDTO;
import com.geomin.demo.dto.ResponseDTO;
import com.geomin.demo.repository.MedicalMaterialRepository;
import com.geomin.demo.service.MedicalMaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class treatmentController {

    private final TreatmentService treatmentService;
    private final MedicalMaterialService medicalMaterialService;

    // 처방(수가 | 의료재료) 팝업창
    @GetMapping("/treatment-search")
    public String getTreatmentPopup(){

        log.info("get >> /treatment-search... getTreatmentPopup()실행됨 ");

        return "treatment_popup";
    }

    // 처방(수가 | 의료재료) 검색
    @PostMapping("/treatment-search")
    public ResponseEntity<?> search(@RequestParam(name = "type") int type ,
                                    @RequestParam(name = "keyword") String keyword) throws IOException {

        log.info("post >> /treatment-search...");
        log.info("type = " + type + " , keyword = " + keyword);

        // 검색진행
        ResponseDTO response = treatmentService.search(type , keyword);

        // 검색 결과 및 200 응답 반환
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }






}
