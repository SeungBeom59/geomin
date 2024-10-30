package com.geomin.demo.service;

import com.geomin.demo.dto.MedicalMaterialDTO;

import java.util.List;

public interface MedicalMaterialService {

    public List<MedicalMaterialDTO> searchMedicalMaterial(String keyword);

}
