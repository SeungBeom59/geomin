package com.geomin.demo.domain;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 진료
public class DiagnosisVO {

    private int diagnosisId;                // 진료번호(pk)
    private PatientVO patient;              // 환자(fk)
    private DoctorVO doctor;                // 의사(fk)
    private DepartmentVO department;        // 소속(fk)
    private String symptoms;                // 증상
    private LocalDateTime diagnosisDate;    // 진료날짜
    private String diagnosis;               // 진료기록
    private String prescription;            // 처방기록
    private Boolean diagnosisYn;            // 진료여부
    private LocalDateTime modifyDate;       // 수정날짜
    private String diagnosisModifier;       // 수정자

}
