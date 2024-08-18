package com.geomin.demo.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ResponseDTO {

    private DiagnosisDTO diagnosisDTO;

    private List<FileInfoDTO> fileInfoDTOList;

    private PatientDTO patientDTO;


}
