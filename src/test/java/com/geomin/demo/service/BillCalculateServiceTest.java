package com.geomin.demo.service;

import com.geomin.demo.domain.BillCalculateResult;
import com.geomin.demo.dto.DiagnosisDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
public class BillCalculateServiceTest {

    @Autowired
    private BillCalculateService billCalculateService;

    private DiagnosisDTO testDTO;

    @BeforeEach
    public void setUpDiagnosis(){

        testDTO = new DiagnosisDTO();
        testDTO.setDiagnosisId(1);
        testDTO.setKcdId(1);
        testDTO.setMedicalBillId(1);
        testDTO.setTreatmentId(1);
        testDTO.setPatientId(16);
    }


    @DisplayName("수납 명세 계산 작업 테스트")
    @Test
    public void calculateBill(){

        BillCalculateResult result = billCalculateService.calculateBill(testDTO);

        assertNotNull(result);
        assertTrue(result.getTotalPay() >= 0);
        log.info("Result :: {}" , result);
    }


}
