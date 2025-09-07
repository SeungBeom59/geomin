package com.geomin.demo.service;

import com.geomin.demo.domain.BillCalculateResult;
import com.geomin.demo.domain.DiagnosisBillVO;
import com.geomin.demo.dto.DiagnosisDTO;
import com.geomin.demo.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
// 진료 기록 수납 내역 서비스
public class DiagnosisBillServiceImpl implements DiagnosisBillService{

    private final BillCalculateService billCalculateService;

    // 수납명세서 생성
    @Override
    public int createDiagnosisBill(ResponseDTO responseDTO) {

        try {
            // 명세서 계산작업
            BillCalculateResult billResult = billCalculateService.calculateBill(responseDTO.getDiagnosisDTO());

            DiagnosisBillVO vo = DiagnosisBillVO.builder()
                    .diagnosisId(responseDTO.getDiagnosisDTO().getDiagnosisId())
                    .totalPay((int)billResult.getTotalPay())
                    .nonBenefit((int)billResult.getNonBenefit())
                    .benefit((int)billResult.getBenefit())
                    .fullSelfPay((int)billResult.getFullSelfPay())
                    .build();


            int cnt = updateDiagnosisBill(billResult);

            return cnt;
        }
        catch (IllegalArgumentException exception){

            responseDTO.setError(true);
            responseDTO.setErrorMsg(exception.getMessage());
            responseDTO.setGuideMsg("진료기록은 저장되었으나, 수납명세서는 작성되지 못했습니다.\n" +
                    "문제상황을 해결하신 후에 수납명세서 작성을 진행해주시길 바랍니다.");

            return -1;
        }
    }


    @Override
    public int updateDiagnosisBill(BillCalculateResult billResult) {
        return 0;
    }
}