package com.geomin.demo.service;

import com.geomin.demo.domain.PatientVO;
import com.geomin.demo.domain.VitalsVO;
import com.geomin.demo.dto.PatientDTO;
import com.geomin.demo.dto.RequestList;
import com.geomin.demo.dto.VitalsDTO;
import com.geomin.demo.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService{

    private final PatientRepository patientRepository;

    // 특정환자를 이름으로 검색하여 페이징된 리스트 받기
    @Override
    public Page<PatientDTO> getPatientList(PatientDTO patientDTO, Pageable pageable) {

        RequestList<?> requestList = RequestList.builder()
                .data(patientDTO)
                .pageable(pageable)
                .build();

        List<PatientVO> result = patientRepository.getPatientList(requestList);

        List<PatientDTO> content = new ArrayList<>();

        for(PatientVO vo : result){
            PatientDTO dto = new PatientDTO();

            dto.setPatientId(vo.getPatientId());
            dto.setPatientName(vo.getPatientName());
            dto.setIdentify(vo.getIdentify());
            dto.setGender(vo.isGender());
            dto.setPhone(vo.getPhone());
            dto.setEmergencyPhone(vo.getEmergencyPhone());
            dto.setAge(vo.getAge());
            dto.setBloodType(vo.getBloodType());
            dto.setAddress(vo.getAddress());
            dto.setAddressDetail(vo.getAddressDetail());
            dto.setPostCode(vo.getPostCode());

            content.add(dto);
        }

//        List<PatientDTO> content = patientRepository.getPatientList(requestList)
//                .stream()
//                .map(PatientDTO::new)
//                .toList();

        int total = patientRepository.getTotal(patientDTO);

        return new PageImpl<>(content, pageable, total);
    }

    // 특정환자 pk를 이용하여 검색, 페이징된 활력징후 리스트 받기
    @Override
    public Page<VitalsDTO> getVitalsList(VitalsDTO vitalsDTO, Pageable pageable) {

        RequestList<?> requestList = RequestList.builder()
                .data(vitalsDTO)
                .pageable(pageable)
                .build();

        List<VitalsVO> result = patientRepository.getVitalsList(requestList);
        log.info("result::{}",result);

        int total = patientRepository.getVitalsTotal(vitalsDTO.getPatientId());

        List<VitalsDTO> content = new ArrayList<>();

        for(VitalsVO vo : result){
            VitalsDTO dto = new VitalsDTO();

            dto.setVitalId(vo.getVitalId());
            dto.setPatientId(vo.getPatientVO().getPatientId());
            dto.setHeight(vo.getHeight());
            dto.setWeight(vo.getWeight());
            dto.setSystolicBlood(vo.getSystolicBlood());
            dto.setDiastolicBlood(vo.getDiastolicBlood());
            dto.setPulse(vo.getPulse());
            dto.setVitalDate(vo.getVitalDate());
            dto.setTemperature(vo.getTemperature());
            dto.setModifyDate(vo.getModifyDate());
            dto.setVitalModifier(vo.getVitalModifier());

            content.add(dto);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public int addVitals(VitalsDTO vitalsDTO) {

        int lastVitalId = patientRepository.geLastVitalId();

        vitalsDTO.setLastVitalId(lastVitalId);

        int result = patientRepository.addVitals(vitalsDTO);

        return result;

    }

    @Override
    public int updateVitals(VitalsDTO vitalsDTO) {

        int result = patientRepository.updateVitals(vitalsDTO);

        return result;
    }

}
