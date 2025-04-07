package com.geomin.demo.domain;

import lombok.*;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// 치료재료청구 vo
public class MedicalBillVO {

    private int medicalBillId;                   // 치료재료청구 번호(pk)
    private int medicalBillSeq;                  // 치료재료청구 일련번호(pk)
    private MedicalMaterialVO medical;           // 치료재료(fk)

    private int usedMmEa;           // 사용갯수
    private boolean usedTreatment;  // 치료행위에 치료재료 사용여부 체크
}
