package com.geomin.demo.service;

import com.geomin.demo.domain.WaitingVO;
import com.geomin.demo.dto.WaitingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;

public interface WaitingService {

    Page<WaitingDTO> getWaitingList(Pageable pageable , int departmentId);

    // 진료 완료 인원 가져오기
    int getEndCount(int departmentId);

    int addWaiting(WaitingDTO waitingDTO , Principal principal);

    // 진료 대기 환자 상태||종류 변경
    int modifyWaitingStatus(WaitingDTO waitingDTO);

    // 진료 접수 기록 id로 찾아 가져오기
    WaitingVO getWaitingById(int waitingId);
}
