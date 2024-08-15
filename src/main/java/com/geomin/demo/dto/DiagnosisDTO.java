package com.geomin.demo.dto;

import com.geomin.demo.domain.DepartmentVO;
import com.geomin.demo.domain.DoctorVO;
import com.geomin.demo.domain.PatientVO;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisDTO {

    private int diagnosisId;                // 진료번호(pk)

    private int patientId;                  // 환자(fk)
    private String patientName;             // 환자명

    private int doctorId;                   // 의사(fk)
    private String doctorName;              // 담당의

    private int departmentId;               // 소속(fk)
    private String departmentName;          // 소속명

    private int waitingId;                  // 진료접수(fk)

    private String symptoms;                // 증상
    private String diagnosisDate;           // 진료날짜

    private String diagnosis;               // 진료기록
    private String prescription;            // 처방기록

    @Builder.Default
    private Boolean diagnosisYn = false;    // 진료여부
    private String modifyDate;              // 수정날짜
    private String diagnosisModifier;       // 수정자

    private int fileId;                     // 파일정보 번호(fk)

    private Boolean diagnosisDelYn;         // 삭제 여부 (1:true 삭제 | 0:false 존재)

    @Builder.Default
    private boolean isSort = true;                 // asc, desc 여부

}
