package com.geomin.demo.domain;

import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class NurseVO {

    private int nurseId;                // 간호사 번호(pk)
    private DepartmentVO department;    // 진료과(fk)
    private String nurseName;           // 간호사 이름
    private String nursePhone;          // 간호사 연락처
}
