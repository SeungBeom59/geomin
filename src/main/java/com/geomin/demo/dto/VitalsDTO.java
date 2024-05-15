package com.geomin.demo.dto;


import com.geomin.demo.domain.PatientVO;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VitalsDTO {

    private int vitalId;                // 활력징후 번호(pk)
    private int patientId;        // 환자정보(fk)
    private double height;              // 신장
    private double weight;              // 몸무게
    private int systolicBlood;          // 혈압수축 수치
    private int diastolicBlood;         // 혈압이완 수치
    private int pulse;                  // 맥박
    private LocalDateTime vitalDate ;       // 측정날짜


}
