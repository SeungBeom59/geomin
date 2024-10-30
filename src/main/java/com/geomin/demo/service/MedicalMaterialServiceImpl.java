package com.geomin.demo.service;

import com.geomin.demo.domain.MedicalMaterialVO;
import com.geomin.demo.dto.MedicalMaterialDTO;
import com.geomin.demo.repository.MedicalMaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MedicalMaterialServiceImpl implements MedicalMaterialService{

    private final MedicalMaterialRepository medicalMaterialRepository;

    @Override
    public List<MedicalMaterialDTO> searchMedicalMaterial(String keyword) {

        List<MedicalMaterialVO> result = null;

        if (keyword.matches("^[A-Za-z]{2}\\d+$") || keyword.matches("^[A-Za-z]\\d+$")){
            result = medicalMaterialRepository.getMedicalByCode(keyword);
        }
        else {
            result = medicalMaterialRepository.getMedicalByName(keyword);
        }

        List<MedicalMaterialDTO> dataList = new ArrayList<>();

        for(MedicalMaterialVO vo : result){

            MedicalMaterialDTO dto = MedicalMaterialDTO.builder()
                    .mmCode(vo.getMmCode())
                    .mmName(vo.getMmName())
                    .manufacturer(vo.getManufacturer())
                    .distributor(vo.getDistributor())
                    .build();

            dataList.add(dto);
        }

        return dataList;
    }
}
