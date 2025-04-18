package com.geomin.demo.domain;

import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MedicalMaterialVO {

    private int mmId;               // 진료재료 번호(pk)
    private String mmCode;          // 진료재료 코드
    private String firstDate;       // 최초등재일
    private String startDate;       // 적용일자
    private String midDivNm;        // 중분류
    private String midDivCode;      // 중분류코드
    private String mmName;          // 치료재료 품명
    private String mmStandard;      // 규격
    private String mmEa;            // 단위
    private int mmMaxPrc;           // 상한금액
    private String manufacturer;    // 제조회사
    private String mmType;          // 재질
    private String distributor;     // 수입회사
    private int mmPrc;              // 설정금액
    // 컬럼 추가, 급여 치료재료 중 본인부담비율, 중복인정여부
    private boolean selfPay50Percent;   // 본인부담율50% 유무
    private boolean selfPay80Percent;   // 본인부담율80% 유무
    private boolean selfPay90Percent;   // 본인부담율90% 우뮤
    private boolean duplicateAllowed;   // 중복인정여부
    private String noticeNumber;        // 고시번호

}
