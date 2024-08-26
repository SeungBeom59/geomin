package com.geomin.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.geomin.demo.domain.PatientVO;
import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MedicineDTO {

    private String entpName;             // 업체명
    private String itemName;             // 의약품명
    private String efcyQesitm;           // 효능
    private String useMethodQesitm;      // 사용법
    private String atpnWarnQesitm;       // 사용전 주의사항
    private String atpnQesitm;           // 사용상 주의사항
    private String intrcQesitm;          // 상호작용(약, 음식)
    private String seQesitm;             // 부작용
    private String depositMethodQesitm;  // 보관법
    private String itemImage;            // 낱알 이미지

    private int itemSeq;                 // 품목기준코드

    private int medicineId;         // 처방전 번호 (pk)
    private int medicineSeq;        // 처방전 일련번호 (pk)
    private String medicineName;    // 약 명칭
    private double dosage;          // 1회 투여량
    private double frequency;       // 1일 투여횟수
    private double days;            // 투약일수


//    private int patientId;          // 환자
//    private String patientName;     // 환자명


}
