package com.geomin.demo.domain;


import com.geomin.demo.dto.DiagnosisDTO;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 진료
public class DiagnosisVO {

    private int diagnosisId;                // 진료번호(pk)
    private PatientVO patient;              // 환자(fk)
    private DoctorVO doctor;                // 의사(fk)
    private DepartmentVO department;        // 소속(fk)
    private WaitingVO waiting;              // 접수(fk) [접수 없이  진료만 받을 수도 있다.]
    private String symptoms;                // 증상
    private LocalDateTime diagnosisDate;    // 진료날짜
    private String diagnosis;               // 진료기록
    private String prescription;            // 처방기록
    private Boolean diagnosisYn;            // 진료여부
    private LocalDateTime modifyDate;       // 수정날짜
    private String diagnosisModifier;       // 수정자
    private int fileId;                     // 첨부파일 번호(일종의 fk: 관계는 맺지 않음)
    private Boolean diagnosisDelYn;         // 삭제 여부 (1:true 삭제 | 0:false 존재)


    public DiagnosisDTO getDateAndModifiedMember(DiagnosisVO vo , DiagnosisDTO dto){

        String diagnosisDate = vo.getDiagnosisDate().toString();
        String time =  diagnosisDate.replace("T" , "/");

        dto.setDiagnosisDate(time);

        if(vo.getModifyDate() != null && vo.getDiagnosisModifier() != null) {
            String modifiedDate = vo.getModifyDate().toString();
            String modifiedTime = modifiedDate.replace("T" , "/");
            dto.setModifyDate(modifiedTime);

            dto.setDiagnosisModifier(vo.getDiagnosisModifier());
        }


        return dto;
    }

}
