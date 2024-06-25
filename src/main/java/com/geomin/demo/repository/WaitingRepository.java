package com.geomin.demo.repository;

import com.geomin.demo.domain.WaitingVO;
import com.geomin.demo.dto.WaitingDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WaitingRepository {

    // 접수대기 가져오기
    List<WaitingVO> getWaitingList(long offset, int departmentId);

    // 접수대기 총 인원 가져오기
    int getTotal(int departmentId);

    // 진료완료 총 인원 가져오기
    int getEndCount(int departmentId);

    // 접수대기 추가하기
    int addWaiting(WaitingDTO waitingDTO);

    // 가장 마지막(최근) pk 값 가져오기
    int getLastWaitingId();

    // 가장 마지막 waitingKey 가져오기
    String getLastWaitingKey();
}
