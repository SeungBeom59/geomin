package com.geomin.demo.domain;

import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MedicineVO {

    private int medicineId;         // 처방전 번호 (pk)
    private int medicineSeq;        // 처방전 일련번호 (pk)
//    private PatientVO patient;      // 환자
    private int itemSeq;            // 품목기준코드
    private String medicineName;    // 약 명칭
    private double dosage;          // 1회 투여량
    private double frequency;       // 1일 투여횟수
    private double days;            // 투약일수

}
