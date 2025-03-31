package com.geomin.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Builder
@Getter
@Setter
public class RequestDTO {

    DiagnosisDTO diagnosisDTO;

    List<MultipartFile> uploadFiles;

    List<Integer> deleteFiles;

    List<MedicineDTO> pills;

    List<KcdDTO> kcds;

    List<TreatmentDTO> treatments;

    List<MedicalMaterialDTO> medicals;

    Principal principal;


}
