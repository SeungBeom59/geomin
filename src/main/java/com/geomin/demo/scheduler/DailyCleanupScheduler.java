package com.geomin.demo.scheduler;

import com.geomin.demo.service.DiagnosisService;
import com.geomin.demo.service.WaitingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
// 미완료된 데이터들을 매일 밤 12시 5분에 처리하는 스케줄러
public class DailyCleanupScheduler {

    private final DiagnosisService diagnosisService;
    private final WaitingService waitingService;

    // todo: 로깅에 남기도록 설정해줄 것
    // 매일 자정 12시 05분에 작성완료하지 않은 진료기록을 삭제 및 로깅
    @Transactional
    @Scheduled(cron = "0 5 0 * * ?")
    public void deleteOldNotFinishedDiagnosis(){

        log.info("DailyCleanupScheduler 작업 시작 (매일 밤 12시 5분에 미완료된 데이터 논리 삭제 처리)");

        try {
            // 전날 미완성 진료기록 논리삭제
            int deleteDiagnosisCnt = diagnosisService.deleteOldNotFinishedDiagnosis();
            // 논리삭제된 전날 진료기록에 연관되는 접수기록 취소처리
            int cancelWaitingCnt = waitingService.cancelOldNotFinishedWaiting();

            log.info("논리삭제 처리된 미작성 진료기록 : {}", deleteDiagnosisCnt);
            log.info("미작성 진료기록 논리삭제에 따른 접수 취소 처리 : {}" , cancelWaitingCnt);
        }
        catch (Exception e){
            log.error("deleteOldNotFinishedData() 실행 에러 발생 - rollback 처리" , e);
            throw e;
        }
    }

}
