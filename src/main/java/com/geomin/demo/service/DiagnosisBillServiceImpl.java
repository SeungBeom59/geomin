package com.geomin.demo.service;

import com.geomin.demo.domain.DiagnosisBillVO;
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

        DiagnosisBillVO result = calculateBill(diagnosisDTO);





        return 0;
    }

    public DiagnosisBillVO calculateBill(DiagnosisDTO diagnosisDTO) {

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

        // todo: 진료비 내용에 대한 명칭 정리 필요!@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        double totalPay = 0;    // 전체 총진료비 (급여 + 비급여)

        double totalBenefitPay = 0;  // 급여진료비 ( 급여 + 급여속 본인부담금)
        double nonBenefit = 0;  // 본인부담금 (급여 중 본인부담금)
        double benefit = 0;     // 공단부담금

        double fullSelfPay = 0;  // 전액본인부담금 (비급여 전액본인부담금)
        double treatmentPercent = 0.3;

        // 상대가치 점수에 이미 요양기관별 점수당 단가 곱해서 나오는 가격으로 계산됨.

        // 급여인지 비급여인지 판별 필요
        for (TreatmentDTO treatment : treatmentBill) {

            if(treatment.isBenefitType()){ // 급여라면 단가를 총진료비에 더하고
                totalBenefitPay += treatment.getUnitPrice();
            }
            else{
                // fixme: 비급여인 항목 본적 있는지? 있다면 값이 존재하는지 알아야함.
                //   - 없다면 그에 따른 값 기록 만들어야 함.
                fullSelfPay += treatment.getFullSelfPay();
            }
        }

        double medicalMaterialPercent = 0.2;

//        // 진료행위에 사용되는 치료재료는 총급여에 포함하여 본인부담률로 나누어 계산
//        // 특정 고시 치료재료 또는 환자가 구매하여 사용하는 치료재료는 따로 고시에 따른 %계산
        for(MedicalMaterialDTO medicalMaterial : medicalBill){


            // 급여
                // 치료행위 중 사용 여부 판별
                    // 특정  선별 치료재료 여부 판별
                        // (특정선별치료재료 o)
                        // 총진료비에 더하여 본인부담률로 나눠 계산 (특정선별치료재료 X)
                // 환자가 구매하여 사용
                    // 특정 선별 치료재료 여부 판별
                        // (특정선별치료재료 O)
                        // (특정선별치료재료 x)
            // 비급여
                // 특정 선별 치료재료 여부 판별
                    // 치료재료에 본인부담률 곱하여 처리 (특정선별치료재료O)
                    // 전액 본인부담 (특정선별치료재료X)

            // 먼저 상한금액(급여)인지 판별
            if(medicalMaterial.getMmMaxPrc() != 0){

                // 치료행위 중 사용여부 판별
                if(medicalMaterial.isUsedTreatment()){
                    // 총급여진료비 + (상한금액 * 사용갯수) 본인부담률 적용
                    totalBenefitPay += medicalMaterial.getMmMaxPrc() * medicalMaterial.getUsedMmEa();
                }
                else{
                    // 진료외 사용 치료재료는 별도 고시한 본인부담률 적용하여 계산
                    fullSelfPay += (medicalMaterial.getMmMaxPrc() * medicalMaterialPercent) * medicalMaterialPercent;
                }
            }
            else{
                if(medicalMaterial.getMmPrc() == 0){
                    throw new IllegalArgumentException("치료재료의 가격이 정해지지 않았습니다.");
                }
                // 비급여라면 환자 전액100% 부담
                fullSelfPay += medicalMaterial.getMmPrc() * medicalMaterial.getUsedMmEa();
            }
        }

        // 나이에 따라 본인 부담금 비율 변화 및 가격에 따른 변화
        treatmentPercent = calculateTreatemntPercent(patient.getAge() , totalBenefitPay);

        // 본인부담금 =  총진료비(단가 총합) * 본인부담금 비율
        nonBenefit = Math.floor(totalBenefitPay * treatmentPercent / 100 ) * 100; // 100원 미만 절사
        // 공단부담금 = totalPay - non
        benefit = Math.floor((totalBenefitPay - nonBenefit) / 100) * 100; // 100원 미만 절사
        // 본인부담금 (비급여)
        fullSelfPay = Math.floor(fullSelfPay / 100) * 100; // 100원 미만 절사

//        DiagnosisBillVO.builder()
//                .diagnosisId(diagnosisDTO.getDiagnosisId())
//                .totalPay((int)totalPay)

        // 총진료비 = (급여)본인부담금 + (급여)공단부담금 + (비급여)본인부담금 + 치료재료
        totalPay = nonBenefit + benefit + fullSelfPay;


        return null;
    }

    // 급여의 본인부담율 계산
    public double calculateTreatemntPercent(int age , double totalPay){

        double treatmentPercent = 0;

        // 65세 이상인 경우
        if(age >= 65){

            // 25,000원 초과인 경우 요양급여비용총액의 30%
            if(totalPay > 2_5000){
                treatmentPercent = 0.3;
            }
            else if(totalPay <= 2_5000 && totalPay > 2_0000){
                treatmentPercent = 0.2;
            }
            else if(totalPay > 1_5000){
                treatmentPercent = 0.1;
            }
            else{
                treatmentPercent = 0;
            }
        }
        else{ // 65세 미만 30%

            // 의약분업 예외환자 처리는 진행하지 않음. (일부 지역에서 병원 + 약제조 하는 곳인 경우 필요)
            // fixme : 임산부인 경우 체크를 위해 환자 상태 컬럼 기록 필요
            // 임산부인 경우 10% (환자의 판별변수가 없음, 일단 넘어감)
            if(age < 1){
                // 1세 미만 5%
                treatmentPercent = 0.05;
            }
            else{
                // 일반환자 30%
                treatmentPercent = 0.3;
            }
        }

        return treatmentPercent;
    }


    @Override
    public int updateDiagnosisBill() {
        return 0;
    }
}
