package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;

import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketHandlerImpl extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(WebSocketHandlerImpl.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket 연결 열림: {}", session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("============================================================================================");
        log.info("Received message: {}", message.getPayload());
        log.info("============================================================================================");

        // 클라이언트로 메시지 전송
        session.sendMessage(new TextMessage("서버에서 올바르게 전달함!"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocket 연결 종료됨: {}", session.getId());
    }
}
