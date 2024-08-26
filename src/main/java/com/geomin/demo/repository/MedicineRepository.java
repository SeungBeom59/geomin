package com.geomin.demo.repository;

import com.geomin.demo.domain.MedicineVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MedicineRepository {

    // 가장 최근 마지막 id 가져오기
    int getLastMedicineId();

    // 의약품 리스트 넣기
    int insertMedicines(List<MedicineVO> medicines);

    // 의약품 결과 가져오기 (목록일 수 있음으로 list로 받는다)
    List<MedicineVO> getMedicineListById(int medicineId);
}
