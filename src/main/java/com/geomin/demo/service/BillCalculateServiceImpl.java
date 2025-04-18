package com.geomin.demo.service;

import com.geomin.demo.config.SpecialKcd;
import com.geomin.demo.domain.BillCalculateResult;
import com.geomin.demo.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
// 수납 명세를 위한 요양급여 계산 서비스
public class BillCalculateServiceImpl implements BillCalculateService{

    private final MedicalMaterialService medicalMaterialService;
    private final TreatmentService treatmentService;
    private final PatientService patientService;
    private final KcdService kcdService;
    private final SpecialKcd specialKcd;

    // 계산절차 진입 메소드
    @Override
    public BillCalculateResult calculateBill(DiagnosisDTO diagnosisDTO) {

        int medicalId = diagnosisDTO.getMedicalBillId();
        int treatmentId = diagnosisDTO.getTreatmentId();

        List<KcdDTO> patientKcd = kcdService.getKcdListById(diagnosisDTO.getKcdId());
        List<MedicalMaterialDTO> medicalBill = medicalMaterialService.getMaterialByBillId(medicalId);
        List<TreatmentDTO> treatmentBill = treatmentService.getTreatmentListById(treatmentId);
        PatientDTO patient = patientService.getPatientById(diagnosisDTO.getPatientId());

        //  수납명세서 작성
        //  - 본인부담금(비급여 진료비, 선택진료료,
        //  진료비총액 , 본인부담금 , 비급여 , 공단부담금(
        //  - 종별가산은 일부 수가코드에만 적용됨.
        //   https://www.hira.or.kr/dummy.do?pgmid=HIRAA030056020110 참조

        BillCalculateResult billResult = new BillCalculateResult(patient.getAge() , patientKcd); // 수납 명세서 누적계산용 vo

        calculateTreatmentBill(billResult, treatmentBill);  // 처방수가 계산
        calculateMedicalBill(billResult , medicalBill);     // 치료재료 계산
        calculateTreatmentPercent(billResult); // 나이와 가격에 따라 본인 부담금 비율 변화
        recalculateForSpecialKcd(billResult , treatmentBill); // 특별 상병에 대한 본인부담률 처리
        billResult.calculateFinalBill(); // 최종 수납 명세 계산 및 100원 미만 절사

        loggingBill(billResult , diagnosisDTO);

        return billResult;
    }

    private void loggingBill(BillCalculateResult billResult , DiagnosisDTO diagnosisDTO){

        log.info("=========================================================================================================");
        log.info("환자 : " + diagnosisDTO.getPatientName() + "[" + diagnosisDTO.getPatientId() + "] 의 수납명세 계산 완료");
        log.info("총진료비 : " + billResult.getTotalPay());
        log.info("총요양급여비용 : " + billResult.getTotalBenefitPay());
        log.info("공단부담금 : " + billResult.getBenefit());
        log.info("본인부담금 : " + billResult.getNonBenefit());
        log.info("전액본인부담금 : " + billResult.getFullSelfPay());
        log.info("=========================================================================================================");

    }

    // 처방수가 계산
    private void calculateTreatmentBill(BillCalculateResult billResult , List<TreatmentDTO> treatmentBill){

        // 상대가치 점수에 요양기관별 점수당 단가 곱해서 나오는 가격으로 단가계산됨.
        // 급여인지 비급여인지 판별 필요
        for (TreatmentDTO treatment : treatmentBill) {

            if(treatment.isBenefitType()){ // 급여라면 단가를 총진료비에 더하고
//                totalBenefitPay += treatment.getUnitPrice();
                billResult.addTotalBenefitPay(treatment.getUnitPrice());
            }
            else{
                // fixme: 비급여인 항목 본적 있는지? 있다면 값이 존재하는지 알아야함.
                //   - 비급여인데 없다면 예외던지고 값을 기입하도록 처리해야함 , 그에 따른 값 기록 만들어야 함.

                if(treatment.getFullSelfPay() == 0){
                    throw new IllegalArgumentException("처방수가/치료행위("+ treatment.getCodeName()+" : "
                            + treatment.getFeeCode() + ")의 가격이 정해지지 않았습니다.");
                }
//                fullSelfPay += treatment.getFullSelfPay();
                billResult.addFullSelfPay(treatment.getFullSelfPay());
            }
        }
    }

    // 치료재료의 본인부담비율 구하기
    private double getMedicalMaterialRate(MedicalMaterialDTO medical){

        if(medical.isSelfPay50Percent()){
            return 0.5;
        }
        else if(medical.isSelfPay80Percent()){
            return 0.8;
        }
        else if(medical.isSelfPay90Percent()){
            return 0.9;
        }
        else{
            return 0.2;
        }

    }

    // 치료재료 계산
    private void calculateMedicalBill(BillCalculateResult billResult , List<MedicalMaterialDTO> medicalBill){

        // 진료행위에 사용되는 치료재료는 총급여에 포함하여 본인부담률로 나누어 계산
        // 특정 고시 치료재료 또는 환자가 구매하여 사용하는 치료재료는 따로 고시에 따른 %계산
        for(MedicalMaterialDTO medicalMaterial : medicalBill) {

            boolean isBenefit = medicalMaterial.getMmMaxPrc() != 0; // 급여 여부
            int acceptCount = medicalMaterial.isDuplicateAllowed() ? medicalMaterial.getUsedMmEa() : 1; // 중복인정여부에 따른 사용갯수
            double medicalRate = getMedicalMaterialRate(medicalMaterial);   // 치료재료 본인부담 비율 구하기


            if (!isBenefit) { // 비급여인 경우

                if (medicalMaterial.getMmPrc() == 0) {
                    // fixme : 비급여인데 불구하고 설정된 가격이 없을 경우 예외 처리
                    //   -  서비스단에서 이를 처리하도록 하고 수납명세서 작성은 종료, 안내메시지 전달 필요
                    throw new IllegalArgumentException(
                            "치료재료(" + medicalMaterial.getMmName() + " : "
                                    + medicalMaterial.getMmCode() + ")의 가격이 정해지지 않았습니다.");
                }
                billResult.addFullSelfPay(medicalMaterial.getMmPrc());

            }
            else { // 급여인 경우

                double totalPrice = medicalMaterial.getMmPrc() * acceptCount; // 치료재료 가격(상한가 * 사용갯수)

                // 특별 고시 치료재료는 수술 여부 따지지 않고 적용
                if (medicalRate > 0.2) {
                    double nonBenefit = totalPrice * medicalRate;
                    double benefit = totalPrice - nonBenefit;

                    billResult.addMaterialNonBenefit(nonBenefit);
                    billResult.addMaterialBenefit(benefit);
                }
                else if (medicalMaterial.isUsedTreatment()) { // 치료행위에 사용여부
                    billResult.addTotalBenefitPay(totalPrice); // 합계는 후에 나이에 따른 본인부담률 계산처리
                }
                else { // 환자 구매 치료재료(소모품 또는 장비등)

                    if (billResult.getPatientAge() < 1) {
                        medicalRate = 0.14;
                    }

                    double nonBenefit = totalPrice * medicalRate;
                    double benefit = totalPrice - nonBenefit;

                    billResult.addMaterialNonBenefit(nonBenefit);
                    billResult.addMaterialBenefit(benefit);
                }

            } // 급여 if else 종료

        } // for문 종료
    }

    // 급여의 본인부담율 계산
    private void calculateTreatmentPercent(BillCalculateResult billResult){

        int age = billResult.getPatientAge();
        // 특별고시 치료재료로 빠졌던 가격도 총요양급여에 포함하여 본인부담률 판별
        double totalPay = billResult.getTotalBenefitPay()
                + billResult.getMaterialBenefit() + billResult.getMaterialNonBenefit();

        // 65세 이상인 경우
        if(age >= 65){

            // 25,000원 초과인 경우 요양급여비용총액의 30%
            if(totalPay > 2_5000){
                return;
            }
            else if(totalPay <= 2_5000 && totalPay > 2_0000){
                billResult.setTreatmentPercent(0.2);
            }
            else if(totalPay > 1_5000){
                billResult.setTreatmentPercent(0.1);
            }
            else{
                billResult.setTreatmentPercent(0);
                // 1500원 본인부담률 가격 설정
                billResult.addNonBenefit(1500);
            }
        }
        else{ // 65세 미만 30%

            // 의약분업 예외환자 처리는 진행하지 않음. (일부 지역에서 병원 + 약제조 하는 곳인 경우 필요)
            // fixme : 임산부인 경우 체크를 위해 환자 상태 컬럼 기록 필요
            // 임산부인 경우 10% (환자의 판별변수가 없음, 일단 넘어감)
            if(age < 1){
                // 1세 미만 5%
                billResult.setTreatmentPercent(0.05);
            }
            else{
                // 일반환자 30%
                return;
            }
        }
    }

    // 특별 고시 상병의 지속 치료행위에 대한 계산
    private void recalculateForSpecialKcd(BillCalculateResult billResult , List<TreatmentDTO> treatmentBill){

        // 65세 이상 고령환자에 25000원 이하인 경우, 메소드 종료
        // 의과 의원, 고혈압(I10)·당뇨(E11) 상병으로 지속 진료하는 경우(시행령 별표2 1 나목 표 하단 비고5) : 해당 진찰료비용의 20%
        // → 요양급여비용총액 25,000원 초과 환자 해당
        if(billResult.getPatientAge() >= 65 && billResult.getTotalBenefitPay() <= 2_5000){
            return;
        }

        List<KcdDTO> patientKcd = billResult.getPatientKcd();
        Map<String , Double> specialKcdMap = specialKcd.getKcd(); // map<특수상병 : 본인부담률>
//        String feeCodePrefix = specialKcd.getFeePrefix();

        // 의사가 진단한 상병 중 특수상병이 존재하는지?
        for(KcdDTO kcd : patientKcd){
            String kcdCode = kcd.getKcdCode();

            // 특별고시된 상병이며, 배제된 상병이 아닌 경우
            if(specialKcd.isTargetSpecialKcd(kcdCode) && kcd.getKcdType() != 3){

                for(TreatmentDTO treatment : treatmentBill){
                    String feeCode = treatment.getFeeCode();

                    // 처방수가에 특별고시된 상병코드인 경우 적용해야하는 처방수가코드가 있다면
                    if(specialKcd.isTargetFeeCode(feeCode)){

                        double price = treatment.getUnitPrice(); // 해당 처방수가 단가

                        // 중복제거 및 개별처리를 위해 기존 총급여진료비 합에서 처방수가 단가 빼기
                        billResult.subTotalBenefitPay(price);

                        // 본인부담금 (특별고시된 상병코드의 본인부담비율로 곱하여 구함)
                        double nonBenefit = price * specialKcdMap.get(kcdCode);
                        // 공단부담금 (단가에 본인부담금을 빼서 공단부담금 구함)
                        double benefit = price - nonBenefit;

                        billResult.addNonBenefit(nonBenefit);
                        billResult.addBenefit(benefit);
                    }
                } // treatment for 끝
            } // if 끝
        }// kcd for 끝

    }
}

// 본인부담률 0인 경우, 고령환자 본인부담금 1500원 고정
//        if(treatmentPercent == 0){
//            benefit = billResult.getTotalBenefitPay() - billResult.getNonBenefit();
//            billResult.addBenefit(benefit);
//        }
//        else{
//            // 본인부담금 = 총급여진료비 * 계산된 본인부담률
//            nonBenefit = billResult.getTotalBenefitPay() * treatmentPercent;
//            benefit = billResult.getTotalBenefitPay() - nonBenefit;
//
//            billResult.addNonBenefit(nonBenefit);   // 개별처리했던 기존 본인부담금 += 계산된 본인부담금
//            billResult.addBenefit(benefit);         // 개별처리했던 공단부담금 += 계산된 공단부담금
//
//            // 따로 구해뒀던 특별고시 치료재료의 공단부담금과 본인부담금도 더해준다.
//            billResult.addBenefit(billResult.getMaterialBenefit());
//            billResult.addNonBenefit(billResult.getMaterialNonBenefit());
//        }
//
//        // 0으로 초기화시켜서 급여진료비를 대기시키고
//        billResult.subTotalBenefitPay(billResult.getTotalBenefitPay());
//        // 총급여진료비를 구한다.
//        billResult.addTotalBenefitPay(billResult.getNonBenefit() + billResult.getBenefit());
//
//        total = billResult.getTotalBenefitPay() + billResult.getFullSelfPay(); // 전체 진료비