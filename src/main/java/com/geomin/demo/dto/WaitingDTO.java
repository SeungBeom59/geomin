package com.geomin.demo.dto;

import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaitingDTO {

    private int waitingId;                  // 접수대기번호
    private int lastWaitingId;              // 마지막 pk

    private int patientId;                  // 환자정보
    private String patientName;
    private String identify;
    private boolean gender;
    private int age;

    private int departmentId;               // 진료과 정보
    private String departmentName;
    private String departmentPhone;

    private String waitingKey;              // 접수대기순서
    private String waitingDate;             // 접수날짜

    private String waitingStatus;           // 접수상태
    private String waitingType;             // 접수종류

    private String symptoms;                // 증상

    private String action;                  // 수정할 접수값

}
