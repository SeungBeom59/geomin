package com.geomin.demo.service;

import com.geomin.demo.dto.MedicineDTO;

import java.util.List;

public interface MedicineService {

    int addMedicine(List<MedicineDTO> pills);

    List<MedicineDTO> getMedicineListById(int medicineId);

    int deleteAndCreateMedicine(int medicineId, List<MedicineDTO> pills);
}
