package com.geomin.demo.domain;

import com.geomin.demo.dto.KcdDTO;
import lombok.*;

import java.util.List;

@Getter
@ToString
// 수납명세서 계산용 객체
// setter는 최대한 사용을 자제하고 롬복의 setter가 아닌 직접 만들어 setter 사용 제한하도록.
// 생성자도 마찬가지.
public class BillCalculateResult{

    double totalPay;                        // 전체 총진료비 (급여 + 비급여)
    double totalBenefitPay;                 // 급여진료비 ( 급여 + 급여속 본인부담금)
    double nonBenefit;                      // 본인부담금 (급여 중 본인부담금)
    double benefit;                         // 공단부담금
    double fullSelfPay;                     // 전액본인부담금 (비급여 전액본인부담금)
    double treatmentPercent = 0.3;          // 의원 기본 본인부담률
    double medicalMaterialPercent = 0.2;    // 치료재료 본인부담률 기본 (영유아는 14%);
    double materialNonBenefit;              // 특별고시치료재료 본인부담금
    double materialBenefit;                 // 특별고시치료재료 공단부담금
    int patientAge;                         // 환자나이
    List<KcdDTO> patientKcd;                // 환자 질병

    //

    // 전체 총 진료비에 더하기(+)
    public void addTotalBenefitPay(double price){
        this.totalBenefitPay += price;
    }
    // 전체 총 진료비에서 빼기(-)
    public void subTotalBenefitPay(double price){
        this.totalBenefitPay -= price;
    }
    // 전액본인부담금에 더하기(+)
    public void addFullSelfPay(double price){
        this.fullSelfPay += price;
    }
    // 본인부담금 더하기(+)
    public void addNonBenefit(double price){
        this.nonBenefit += price;
    }
    // 공단부담금 더하기(+)
    public void addBenefit(double price){
        this.benefit += price;
    }
    // 특별고시치료재료 본인부담금(+)
    public void addMaterialNonBenefit(double price){
        this.materialNonBenefit += price;
    }
    // 특별고시치료재료 공단부담금(+)
    public void addMaterialBenefit(double price){
        this.materialBenefit += price;
    }

    // 의원 본인부담률 설정(setter)
    public void setTreatmentPercent(double percent){
        this.treatmentPercent = percent;
    }

    // percent 를 이용하여 계산
    public void calculateFinalBill(){

        double benefit;
        double nonBenefit;

        if(this.treatmentPercent == 0){
            // 65세 고령 환자가 총요양급여비용 15000원 이하인 경우, 본인부담금 1500원 고정
            benefit = this.totalBenefitPay - this.nonBenefit;
            // 본인부담금 1500원을 제외한 나머지는 공단부담금
            this.benefit += benefit;
        }
        else{
            // 본인부담금 = 총급여진료비 * 계산된 본인부담률
            nonBenefit = this.totalBenefitPay * this.treatmentPercent;
            // 공단부담금 = 총급여진료비의 본인부담금 차액
            benefit = this.totalBenefitPay - nonBenefit;

            // 특정 상병에 대하여 고시된 부담률로 이미 구해뒀던 본인부담금과 공단부담금에 더한다.
            this.nonBenefit += nonBenefit;
            this.benefit += benefit;
        }

        // 따로 구해뒀던 특별고시 치료재료의 공단부담금과 본인부담금도 더해준다.
        this.benefit += this.materialBenefit;
        this.nonBenefit += this.materialNonBenefit;

        //
        this.totalBenefitPay -= this.totalBenefitPay;

        this.totalBenefitPay += this.nonBenefit + this.benefit;
        this.totalPay += this.totalBenefitPay + this.fullSelfPay;

        this.truncateUnderHumdred();
    }

    // 명세서에 사용되는 필드들 모두 100원 미만 절사 처리
    public void truncateUnderHumdred(){
        this.totalBenefitPay = truncate(this.totalBenefitPay);
        this.totalPay = truncate(this.totalPay);
        this.nonBenefit = truncate(this.nonBenefit);
        this.benefit = truncate(this.benefit);
        this.fullSelfPay = truncate(this.fullSelfPay);
    }
    // 100원 미만 절사
    public double truncate(double price){
        return Math.floor(price / 100) * 100;
    }

    // 환자 나이와 상병 목록만 설정하여 생성가능토록.
    public BillCalculateResult(int age , List<KcdDTO> kcds){
        this.patientAge = age;
        this.patientKcd = kcds;
    }

    // 기본생성자 사용불가
    private BillCalculateResult(){
        // 기본 생성자는 사용불가하도록, 접근제한자 private로
    }

}
