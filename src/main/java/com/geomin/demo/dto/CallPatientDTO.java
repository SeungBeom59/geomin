package com.geomin.demo.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallPatientDTO {

    private PatientDTO patient;
    private DiagnosisDTO todayDiagnosis;

}
