package com.geomin.demo.service;

import com.geomin.demo.domain.MedicalBillVO;
import com.geomin.demo.domain.MedicalMaterialVO;
import com.geomin.demo.dto.MedicalMaterialDTO;
import com.geomin.demo.repository.MedicalMaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class MedicalMaterialServiceImpl implements MedicalMaterialService{

    private final MedicalMaterialRepository medicalMaterialRepository;

    // 치료재료 정보 keyword 판별하여 검색하기
    @Override
    public List<MedicalMaterialDTO> searchMedicalMaterial(String keyword) {

        List<MedicalMaterialVO> result = null;
        // keyword가 영문 1~2글자 이후 숫자 연속된다면 코드명 검색, 아니라면 치료재료명 검색으로 분류.
        if (keyword.matches("^[A-Za-z]{2}\\d+$") || keyword.matches("^[A-Za-z]\\d+$")){
            result = medicalMaterialRepository.getMedicalByCode(keyword);
        }
        else {
            result = medicalMaterialRepository.getMedicalByName(keyword);
        }

        // 검색하여 받은 vo -> dto로 변환하여 return.
        List<MedicalMaterialDTO> dataList = new ArrayList<>();

        for(MedicalMaterialVO vo : result){

            MedicalMaterialDTO dto = MedicalMaterialDTO.builder()
                    .mmId(vo.getMmId())
                    .mmCode(vo.getMmCode())
                    .mmName(vo.getMmName())
                    .manufacturer(vo.getManufacturer())
                    .distributor(vo.getDistributor())
                    .mmPrc(vo.getMmPrc())
                    .build();

            dataList.add(dto);
        }

        return dataList;
    }

    // 치료재료 정보 db에 insert 하기
    @Override
    public int insertMedicalBills(List<MedicalMaterialDTO> medicals) {

        // 치료재료 정보 검색은 치료정보 테이블에서 가져오지만, 기록은 치료재료청구 테이블에 함으로
        // 치료재료청구 테이블의 마지막 번호를 가져오고 해당 테이블에 medicals의 mmId 들을 넣어준다.
        // 해당 테이블은 치료재료청구(pk) , 치료재료번호(fk)로 이뤄져 있음.
        int lastMedicalBillId = medicalMaterialRepository.getLastMedicalBillId();

        List<MedicalBillVO> medicalBillVOS = new ArrayList<>();
        AtomicInteger medicalBillSeq = new AtomicInteger(1);

        for (MedicalMaterialDTO medical : medicals) {

            MedicalMaterialVO vo = MedicalMaterialVO.builder()
                    .mmId(medical.getMmId())
                    .mmCode(medical.getMmCode())
                    .mmName(medical.getMmName())
                    .build();

            MedicalBillVO medicalVO = MedicalBillVO.builder()
                    .medicalBillId(lastMedicalBillId + 1)
                    .medicalBillSeq(medicalBillSeq.getAndIncrement())
                    .medical(vo)
                    .build();

            medicalBillVOS.add(medicalVO);
        }
        // 치료재료청구 테이블에 insert 하여 성공한 레코드 갯수 반환값 받아오기
        int successCnt = medicalMaterialRepository.insertMedicalBills(medicalBillVOS);

        if(successCnt != medicals.size()){
            log.error("insertMedicalBills() failed");
            throw new IllegalArgumentException("insertMedicalBills() failed");
        }

        return lastMedicalBillId + 1;
    }

    // 치료재료청구 테이블의 검색되는 pk 레코드 삭제 후, 새로운 값으로 insert 또는 비우기.
    @Override
    public int deleteAndCreateMedicalBill(int medicalBillId, List<MedicalMaterialDTO> medicals) {

        // 치료재료청구 테이블에서 기록 삭제
        medicalMaterialRepository.deleteByMedicalBillId(medicalBillId);

        // medicals에 아무것도 없다면 diagnosis에 -1이 리턴되도록 반환.
        // diagnosis db작업 sql에서 -1이면 아무값도 없도록 변경처리 진행.
        if(medicals == null || medicals.size() <= 0) {
            return -1;
        }

        List<MedicalBillVO> medicalBillVOS = new ArrayList<>();
        AtomicInteger medicalBillSeq = new AtomicInteger(1);

        for (MedicalMaterialDTO medical : medicals) {

            MedicalMaterialVO vo = MedicalMaterialVO.builder()
                    .mmId(medical.getMmId())
                    .mmCode(medical.getMmCode())
                    .mmName(medical.getMmName())
                    .build();

            MedicalBillVO medicalVO = MedicalBillVO.builder()
                    .medicalBillId(medicalBillId)
                    .medicalBillSeq(medicalBillSeq.getAndIncrement())
                    .medical(vo)
                    .build();

            medicalBillVOS.add(medicalVO);
        }
        // 치료재료청구 테이블에 insert 하여 성공한 레코드 갯수 반환값 받아오기
        int successCnt = medicalMaterialRepository.insertMedicalBills(medicalBillVOS);

        if(successCnt != medicals.size()){
            log.error("deleteAndCreateMedicalBill() failed");
            throw new IllegalArgumentException("insertMedicalBills() failed");
        }

        return medicalBillId;
    }

    // 치료재료청구 pk로 레코드 조회하여, 해당 레코드의 참조되는 치료재료 목록 가져오기
    @Override
    public List<MedicalMaterialDTO> getMaterialByBillId(int medicalBillId) {

        List<MedicalBillVO> medicalBillVOS = medicalMaterialRepository.getMaterialByBillId(medicalBillId);

        List<MedicalMaterialDTO> medicalMaterialDTOS = new ArrayList<>();

        for (MedicalBillVO billVo : medicalBillVOS) {

            MedicalMaterialVO vo = billVo.getMedical();

            MedicalMaterialDTO dto = MedicalMaterialDTO.builder()
                    .mmId(vo.getMmId())
                    .mmCode(vo.getMmCode())
                    .firstDate(vo.getFirstDate())
                    .midDivNm(vo.getMidDivNm())
                    .midDivCode(vo.getMidDivCode())
                    .mmName(vo.getMmName())
                    .mmStandard(vo.getMmStandard())
                    .mmEa(vo.getMmEa())
                    .mmMaxPrc(vo.getMmMaxPrc())
                    .manufacturer(vo.getManufacturer())
                    .mmType(vo.getMmType())
                    .distributor(vo.getDistributor())
                    .mmPrc(vo.getMmPrc())
                    .usedMmEa(billVo.getUsedMmEa())
                    .usedTreatment(billVo.isUsedTreatment())
                    .build();

            medicalMaterialDTOS.add(dto);
        }

        return medicalMaterialDTOS;
    }


}
