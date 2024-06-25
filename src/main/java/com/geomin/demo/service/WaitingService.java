package com.geomin.demo.service;

import com.geomin.demo.dto.WaitingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WaitingService {

    Page<WaitingDTO> getWaitingList(Pageable pageable , int departmentId);

    // 진료 완료 인원 가져오기
    int getEndCount(int departmentId);

    int addWaiting(WaitingDTO waitingDTO);
}
