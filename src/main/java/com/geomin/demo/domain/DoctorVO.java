package com.geomin.demo.domain;

import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class DoctorVO {

    private int doctorId;               // 의사 번호(pk)
    private DepartmentVO department;    // 진료과 (fk)
    private String doctorName;  // 의사 이름
    private String doctorPhone; // 의사 연락처
}
