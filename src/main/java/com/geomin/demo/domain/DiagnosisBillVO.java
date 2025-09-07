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
    private int totalPay;               // 전체 총 진료비 (급여 + 비급여)
    private int nonBenefit;             // 본인부담금 (급여 중 본인부담금)
    private int benefit;                // 공단부담금
    private int fullSelfPay;            // 전액본인부담금 (비급여 전액본인부담금)
    private int payStatus;              // 수납상태


    //    private int nonBenefit;             // 비급여진료비
//    private int deductible;             // 본인부담금 (급여 중 본인분담금)
}
