package com.geomin.demo.domain;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientVO {

    private int patientId;          // 환자번호
    private String patientName;     // 환자이름
    private String identify;        // 주민등록번호
    private boolean gender;         // 성별(true,1 == 남자 / false,2 == 여자)
    private String phone;           // 연락처
    private String emergencyPhone;  // 비상연락처
    private int age;                // 나이
    private String bloodType;       // 혈액형
    private String address;         // 주소
    private String addressDetail;   // 상세주소
    private String postCode;        // 우편번호

}
