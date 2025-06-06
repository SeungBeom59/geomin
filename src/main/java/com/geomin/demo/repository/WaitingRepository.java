package com.geomin.demo.repository;

import com.geomin.demo.domain.WaitingVO;
import com.geomin.demo.dto.WaitingDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WaitingRepository {

    // 접수대기 가져오기 (size 4 고정, sql에 하드코딩으로 넣어둠)
    List<WaitingVO> getWaitingList(long offset, int departmentId);

    // 접수완료 목록 가져오기 (size 4 고정)
    List<WaitingVO> getEndWaitingList(int offset, int departmentId);

    // 접수대기 총 인원 가져오기
    int getWaitingTotal(int departmentId);

    // 진료완료 총 인원 가져오기
    int getEndCount(int departmentId);

    // 접수대기 추가하기
    int addWaiting(WaitingDTO waitingDTO);

    // 가장 마지막(최근) pk 값 가져오기
    int getLastWaitingId();

    // 가장 마지막 waitingKey 가져오기
    String getLastWaitingKey();

    // waitingStatus 변경
    int updateWaitingStatus(WaitingVO vo);

    // waitingType 변경
    int updateWaitingType(WaitingVO vo);

    // 방금 생성된 유니크 키로 집어넣었던 waiting 가져오기
    WaitingVO getWaitingByKey(String newWaitingKey);

    // 접수대기 id로 가져오기
    WaitingVO getWaitingById(int waitingId);

    // 진료를 받지 않고 방치된 접수 취소처리 (스케줄러)
    int cancelOldNotFinishedWaiting();
}
