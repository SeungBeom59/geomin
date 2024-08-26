package com.geomin.demo.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
// 진료기록 하나를 보기 위해 가져가야할 모든 dto
public class ResponseDTO {

    private DiagnosisDTO diagnosisDTO;              // 진료기록

    private List<FileInfoDTO> fileInfoDTOList;      // 첨부파일 정보

    private PatientDTO patientDTO;                  // 환자정보

    private List<MedicineDTO> medicineDTOList;      // 처방정보

}
