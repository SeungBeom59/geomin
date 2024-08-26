package com.geomin.demo.service;

import com.geomin.demo.domain.MedicineVO;
import com.geomin.demo.dto.MedicineDTO;
import com.geomin.demo.repository.MedicineRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;

    @Override
    public int addMedicine(List<MedicineDTO> pills) {

        log.info("addMedicine 실행");

        // 마지막 pk(id) 가져오기
        int lastMedicineId = medicineRepository.getLastMedicineId();

        List<MedicineVO> medicines = new ArrayList<>();
        AtomicInteger medicineSeq = new AtomicInteger(1);

        pills.forEach(pill -> {

            MedicineVO medicine = MedicineVO.builder()
                    .medicineId(lastMedicineId + 1)
                    .medicineSeq(medicineSeq.getAndIncrement())
                    .itemSeq(pill.getItemSeq())
                    .medicineName(pill.getMedicineName())
                    .dosage(pill.getDosage())
                    .frequency(pill.getFrequency())
                    .days(pill.getDays())
                    .build();

            medicines.add(medicine);
        });

        int successCnt = medicineRepository.insertMedicines(medicines);

        if(successCnt != medicines.size()) {
            log.error("addMedicine failed");
            throw new IllegalArgumentException("addMedicine failed");
        }

        return lastMedicineId + 1;
    }

    @Override
    public List<MedicineDTO> getMedicineListById(int medicineId) {

        log.info("getMedicineListById 실행");

        List<MedicineVO> medicineVOs = medicineRepository.getMedicineListById(medicineId);

        List<MedicineDTO> medicineDTOs = new ArrayList<>();

        medicineVOs.forEach(medicineVO -> {

            MedicineDTO medicineDTO = MedicineDTO.builder()
                    .medicineId(medicineVO.getMedicineId())
                    .medicineSeq(medicineVO.getMedicineSeq())
                    .itemSeq(medicineVO.getItemSeq())
                    .medicineName(medicineVO.getMedicineName())
                    .dosage(medicineVO.getDosage())
                    .frequency(medicineVO.getFrequency())
                    .days(medicineVO.getDays())
                    .build();

            medicineDTOs.add(medicineDTO);
        });


        return medicineDTOs;
    }


}
