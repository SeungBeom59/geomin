package com.geomin.demo.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
// 의료재료
public class MedicalMaterialDTO {

    private String mmCoed;          // 진료재료 코드
    private String firstDate;         // 최초등재일
    private String startDate;         // 적용일자
    private String midDivNm;        // 중분류
    private String midDivCode;      // 중분류코드
    private String mmName;          // 치료재료 품명
    private String mmStandard;      // 규격
    private String mmEa;            // 단위
    private int mmMaxPrc;           // 상한금액
    private String manufacturer;    // 제조회사
    private String mmType;          // 재질
    private String distributor;     // 수입회사


}
