package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.SockJsService;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // WebSocket 핸들러 등록
        registry.addHandler(new WebSocketHandlerImpl(), "/ws") // WebSocket 엔드포인트를 "/ws"로 설정
                .setAllowedOrigins("http://localhost:3000", "http://www.kcs2.co.kr") // 허용할 도메인 설정
                .withSockJS(); // SockJS를 사용하여 WebSocket 연결을 지원
    }
}
