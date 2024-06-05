package com.geomin.demo.service;

import com.geomin.demo.dto.PatientDTO;
import com.geomin.demo.dto.VitalsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientService {

//    List<PatientDTO> getPatientList(String patientName);

    Page<PatientDTO> getPatientList(PatientDTO patientDTO, Pageable pageable);

    Page<VitalsDTO> getVitalsList(VitalsDTO vitalsDTO , Pageable pageable);

    int addVitals(VitalsDTO vitalsDTO);

    int updateVitals(VitalsDTO vitalsDTO);
}
