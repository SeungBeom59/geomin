package com.geomin.demo.domain;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
// 질병기록 vo
public class KcdVO {

    private int kcdId;          // 질병기록 번호  (pk)
    private int kcdSeq;         // 질병기록 일련번호 (pk)
    private String kcdName;     // 질병명칭
    private String kcdCode;     // 질병코드 (3단계 분류 3글자(영문1+숫자2), 4단계 분류 4글자(영문1+숫자3)
    private int kcdType;        // 질병상태 (주상, 부상, 배제)
    private int kcdRank;        // 질병순위 {(1 ~ ?) 1이 주상, 2부터 부상, 배제}
//    private PatientVO patient;  // 환자정보 (fk)


}
