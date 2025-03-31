package com.geomin.demo.service;

import com.geomin.demo.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
// 진료 기록 수납 내역 서비스
public class DiagnosisBillServiceImpl implements DiagnosisBillService{

    private final MedicalMaterialService medicalMaterialService;
    private final TreatmentService treatmentService;
    private final PatientService patientService;


    @Override
    public int createDiagnosisBill(DiagnosisDTO diagnosisDTO) {

        int medicalId = diagnosisDTO.getMedicalBillId();
        int treatmentId = diagnosisDTO.getTreatmentId();

        List<MedicalMaterialDTO> medicalBill = medicalMaterialService.getMaterialByBillId(medicalId);
        List<TreatmentDTO> treatmentBill = treatmentService.getTreatmentListById(treatmentId);
        PatientDTO patient = patientService.getPatientById(diagnosisDTO.getPatientId());

        // todo : 수납명세서 작성
        //  - 본인부담금(비급여 진료비, 선택진료료,
        //  진료비총액 , 본인부담금 , 비급여 , 공단부담금(
        //  - 종별가산은 일부 수가코드에만 적용됨.
        //   https://www.hira.or.kr/dummy.do?pgmid=HIRAA030056020110 참조

        log.info("medicalBill" + medicalBill);
        log.info("treatmentBill" + treatmentBill);


        double totalPay = 0;    // 총진료비
        double nonBenefit = 0;  // 본인부담금 (급여 중 본인부담금)
        double deductible = 0;  // 전액본인부담금 (비급여 전액본인부담금)
        double benefit = 0;     // 공단부담금

//
//        // 급여인지 비급여인지 판별 필요
//        for(TreatmentDTO treatment : treatmentBill){
//
//            if(!treatment.getBenefit()){
//                totalPay +=
//            }
//
//
//            // 종별가산 적용인지 판별 필요
//        }




        return 0;
    }



    @Override
    public int updateDiagnosisBill() {
        return 0;
    }
}
