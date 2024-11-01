package com.geomin.demo.repository;

import com.geomin.demo.domain.MedicalMaterialVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MedicalMaterialRepository {

    // 치료재료명으로 검색하기 (LIMIT 10개)
    List<MedicalMaterialVO> getMedicalByName(String keyword);

    // 치료재료코드로 검색하기 (LIMIT 10개)
    List<MedicalMaterialVO> getMedicalByCode(String keyword);


}
