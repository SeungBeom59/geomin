package com.geomin.demo.controller;

import com.geomin.demo.dto.ResponseDTO;
import com.geomin.demo.dto.WaitingDTO;
import com.geomin.demo.service.WaitingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final WaitingService waitingService;

    // 목표 :
    //      1. 새로운 환자가 접수될 때,
    //      2. 환자의 진료가 완료될 때,
    //      3. 대기환자의 정보가 변경될 때,
    //      4. 진료완료(수납대기환자)의 정보가 변경될 때
    //      webSocket 을 통해 실시간으로 알림을 보낸다.

    // 클라이언트가 send 하는 경로
    // WebSocketConfig에서 설정한 applicationDestinationPrefixes 경로와 함께 사용됨.
    // /pub/waiting/{departmentId}
    @MessageMapping("/waiting/{departmentId}") // 클라이언트에서 매핑으로 메세지를 보내면 메소드가 실행됨
    public void notifyWaitingListUpdate(@DestinationVariable int departmentId ,
                                        @Header("simpSessionId") String sessionId){

        log.info("웹소켓 핸들러 작동함###########################################################");
//        // 페이지에이블 직접 생성 필요
        Pageable pageable = PageRequest.of(0,4);
        Page<WaitingDTO> waitingList = waitingService.getWaitingList(pageable , departmentId);
        log.info("waitingList::{}" , waitingList.stream().toList());

        ResponseDTO responseDTO = waitingService.getEndWaitingList(0 , departmentId);
        responseDTO.setWaitingDTOS(waitingList);

        messagingTemplate.convertAndSend("/sub/waiting/" + departmentId , responseDTO);
    }
}
