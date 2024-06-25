package com.geomin.demo.domain;

import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class DepartmentVO {

    private int departmentId;       // 진료과번호(pk)
    private String departmentName;  // 진료과 이름
    private String departmentPhone; // 진료과 연락처
}
