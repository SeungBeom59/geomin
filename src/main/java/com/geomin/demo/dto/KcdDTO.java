package com.geomin.demo.dto;

import com.geomin.demo.domain.PatientVO;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 질병기록 DTO
public class KcdDTO {

    private int kcdId;          // 질병기록 번호  (pk)
    private int kcdSeq;         // 질병기록 일련번호 (pk)
    private String kcdName;     // 질병명칭
    private String kcdCode;     // 질병코드 (3단계 분류 3글자(영문1+숫자2), 4단계 분류 4글자(영문1+숫자3)
    private int kcdType;        // 질병상태 (1: 주상, 2: 부상, 3: 배제)
    private int kcdRank;        // 질병순위 {(1 ~ ?)}

//    private int patientId;      // 환자번호 (fk)
//    private String patientName; // 환자명
}
