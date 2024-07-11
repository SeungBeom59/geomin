package com.geomin.demo.domain;

import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// 접수대기
public class WaitingVO {

    private int waitingId;              // 접수대기번호(pk)
    private PatientVO patient;          // 환자 정보(fk)
    private DepartmentVO department;    // 진료과(fk)
    private String waitingKey;          // 접수대기순서
    private LocalDateTime waitingDate;  // 접수날짜
    private int waitingStatus;          // 접수상태 (0: 대기중 , 1: 진료중 , 2: 진료완료 , 3:검사중 , 4:검사대기)
    private int waitingType;            // 접수종류 (0: 일반진료, 1: 전문진료 , 2: 입원진료, 3: 응급진료, 4: 외래진료, 5:재활진료... 등)

}
