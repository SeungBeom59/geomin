package com.geomin.demo.dto;

import com.geomin.demo.domain.MedicalMaterialVO;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 치료재료청구 dto
public class MedicalBillDTO {

    private int medicalBillId;                   // 치료재료청구 번호(pk)
    private int medicalBillSeq;                  // 치료재료청구 일련번호(pk)
    private int mmId;                            // 치료재료 번호
    private String mmCode;                       // 치료재료 품명
    private String manufacturer;                 // 제조회사
    private String distributor;                  // 수입회사
    private int mmMaxPrc;                       // 상한금액
    // 추가됨
    private int usedMmEa;                        // 사용갯수
    private boolean usedTreatment;              // 치료행위에 치료재료 사용여부 체크

}
