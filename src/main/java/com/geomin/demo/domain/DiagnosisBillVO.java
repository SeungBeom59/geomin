package com.geomin.demo.domain;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 수납내역
public class DiagnosisBillVO {

    private int diagnosisBillId;        // 수납내역번호(pk)
    private int diagnosisId;            // 진료기록번호(fk : 관계는 맺지 않음)
    private int deductible;             // 본인부담금 (급여 중 본인분담금)
    private int fullSelfPay;            // 전액본인부담금 (비급여 전액본인부담금)
    private int nonBenefit;             // 비급여진료비
    private int benefit;                // 공단부담금
    private int totalPay;               // 진료비총액
    private int payStatus;              // 수납상태

}
