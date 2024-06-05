package com.geomin.demo.repository;

import com.geomin.demo.controller.HomeController;
import com.geomin.demo.domain.PatientVO;
import com.geomin.demo.domain.VitalsVO;
import com.geomin.demo.dto.PatientDTO;
import com.geomin.demo.dto.RequestList;
import com.geomin.demo.dto.VitalsDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@SpringBootTest
public class PatientRepositoryTests {

    @Autowired
    private PatientRepository patientRepository;

    @Test
    public void findByName(){

        String patientName = "임승범";

        List<PatientVO> patients = patientRepository.findByName(patientName);

        for(PatientVO patient : patients){
            System.out.println(patient.toString());
        }
    }

    @Test
    public void getVitalsList(@PageableDefault(size = 1) Pageable pageable){

        VitalsDTO vitalsDTO = new VitalsDTO();
        vitalsDTO.setPatientId(2);

        RequestList<?> requestList = RequestList.builder()
                .data(vitalsDTO)
                .pageable(pageable)
                .build();

        List<VitalsVO> result = patientRepository.getVitalsList(requestList);

        System.out.println("result = " + result.toString());

    }



}
