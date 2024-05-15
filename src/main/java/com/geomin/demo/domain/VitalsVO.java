package com.geomin.demo.domain;

import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 활력 징후
public class VitalsVO {

    private int vitalId;                // 활력징후 번호(pk)
    private PatientVO patientVO;        // 환자정보(fk)
    private double height;              // 신장
    private double weight;              // 몸무게
    private int systolicBlood;          // 혈압수축 수치
    private int diastolicBlood;         // 혈압이완 수치
    private int pulse;                  // 맥박
    private LocalDateTime vitalDate ;       // 측정날짜

}
