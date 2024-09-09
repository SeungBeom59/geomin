package com.geomin.demo.service;

import com.geomin.demo.domain.KcdVO;
import com.geomin.demo.dto.KcdDTO;
import com.geomin.demo.repository.KcdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class KcdServiceImpl implements KcdService {

    private final KcdRepository kcdRepository;


    @Override
    public List<KcdDTO> getKcdListById(int kcdId) {

        List<KcdVO> kcdVOS = kcdRepository.getKcdListById(kcdId);

        List<KcdDTO> kcdDTOs = new ArrayList<>();

        kcdVOS.forEach(kcdVO -> {

            KcdDTO kcdDTO = KcdDTO.builder()
                    .kcdId(kcdVO.getKcdId())
                    .kcdSeq(kcdVO.getKcdSeq())
                    .kcdName(kcdVO.getKcdName())
                    .kcdCode(kcdVO.getKcdCode())
                    .kcdType(kcdVO.getKcdType())
                    .kcdRank(kcdVO.getKcdRank())
                    .build();

            kcdDTOs.add(kcdDTO);
        });


        return kcdDTOs;
    }

    @Override
    public int addKcd(List<KcdDTO> kcds) {

        int lastKcdId = kcdRepository.getLastKcdId();

        List<KcdVO> kcdVOs = new ArrayList<>();
        AtomicInteger kcdSeq = new AtomicInteger(1);

        kcds.forEach(kcdDTO -> {

            KcdVO kcdVO = KcdVO.builder()
                    .kcdId(lastKcdId + 1)
                    .kcdSeq(kcdSeq.getAndIncrement())
                    .kcdName(kcdDTO.getKcdName())
                    .kcdCode(kcdDTO.getKcdCode())
                    .kcdType(kcdDTO.getKcdType())
                    .kcdRank(kcdDTO.getKcdRank())
                    .build();

            kcdVOs.add(kcdVO);
        });

        log.info("kcdVOs::{}", kcdVOs);
        int successCnt = kcdRepository.insertKcds(kcdVOs);

        if(successCnt != kcds.size()) {
            log.error("addKcds() failed");
            throw new IllegalArgumentException("addKcds() failed");
        }

        return lastKcdId + 1;
    }

    @Override
    public int deleteAndCreateKcds(int kcdId, List<KcdDTO> kcds) {

        // 기존 kcd 기록 삭제
        kcdRepository.deleteById(kcdId);

        // kcds 리스트에 아무것도 없다면 그대로 두어라.
        if(kcds == null || kcds.size() <= 0){
            // 해당 diagnosisDTO에 kcdId는 이제 없음으로 null로 만들기 위해 -1 리턴
            // sql에서 처리하도록 했음.
            return -1;
        }

        List<KcdVO> kcdVOs = new ArrayList<>();
        AtomicInteger kcdSeq = new AtomicInteger(1);

        kcds.forEach(kcdDTO -> {

            KcdVO kcdVO = KcdVO.builder()
                    .kcdId(kcdId)
                    .kcdSeq(kcdSeq.getAndIncrement())
                    .kcdName(kcdDTO.getKcdName())
                    .kcdCode(kcdDTO.getKcdCode())
                    .kcdType(kcdDTO.getKcdType())
                    .kcdRank(kcdDTO.getKcdRank())
                    .build();

            kcdVOs.add(kcdVO);
        });

        log.info("kcdVOs::{}", kcdVOs);

        int successCnt = kcdRepository.insertKcds(kcdVOs);
        if(successCnt != kcds.size()) {
            log.error("addKcds() failed");
            throw new IllegalArgumentException("addKcds() failed");
        }
        return kcdId;
    }
}


























