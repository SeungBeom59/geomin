package com.geomin.demo.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TreatmentDTO {

    private int treatmentId;            // 처방수가번호(pk)
    private int treatmentSeq;                // 처방수가 일련번호(pk)
    private Date startDate;             // 적용시작날짜
    private boolean benefitType;        // 급여구분 (급여|비급여)
    private String feeCode;             // 수가코드
    private String feeDivNum;           // 수가분류번호
    private boolean surgeryYn;          // 수술구분
    private int unitPrice;              // 단가
    private String unitPriceType;       // 단가구분
    private double costScore;           // 상대가치점수
    private String codeName;            // 수가명
    private int benefit;                // 공단부담금
    private int deductible;             // 본인부담금 (급여 중 본인분담금)
    private int fullSelfPay;            // 전액본인부담금 (비급여 전액본인부담금)
    private boolean deductibleA;        // 본인부담율 A항 구분
    private boolean deductibleB;        // 본인부담율 B항 구분
    private int nonBenefit;             // 비급여진료비
    private int optionalFee;            // 선택진료료
    private int additionalOptionFee;    // 선택진료료 외





}
