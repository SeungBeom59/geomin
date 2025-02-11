package com.geomin.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// 자바 설정 파일
@Configuration
// 웹 소켓 메시지 브로커 기능 활성화
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    // 한 클라이언트에서 다른 클라이언트로 메세지를 라우팅 할 때 사용하는 브로커(전달책?)
    public void configureMessageBroker(MessageBrokerRegistry config){
        // @controller의 @MessageMapping으로 라우팅 되는 경로
        // 해당 경로를 simpleBroker에 등록, 해당 경로를 subscribe하는 모든 클라이언트에게 메세지 전달하는 기능 수행.
        // 메세지를 구독하는 요청 설정
        config.enableSimpleBroker("/sub");

        // 메세지를 발행하는 요청 설정
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    // 클라이언트에서 websocket에 접속하는 endpoint 등록
    public void registerStompEndpoints(StompEndpointRegistry registry){

        // 웹 소켓 연결을 위한 엔드포인트 설정
        registry
                .addEndpoint("/ws") // 핸드셰이크를 위한 endpoint 경로, 커넥션 생성 위해.
                .setAllowedOrigins("http://localhost:8080" ,  "http://*.*.*.*:8080") // cors 정책으로 인해 허용 도메인을 지정해야 함.
                .withSockJS(); // 낮은 버전의 브라우저도 문제 없이 websocket 사용하도록 sockJs
                              // SockJs는 webSocket을 활용하여 통신을 시도하고 실패 할 경우, HTTP 기반의 다른 기술로 전환하여
                              // 재연결을 시도하는 방식인 WebSocket Emulation 기술을 제공하는 Js의 라이브러리.
    }


}
