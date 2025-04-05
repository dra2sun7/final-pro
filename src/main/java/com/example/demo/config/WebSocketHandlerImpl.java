package com.example.demo.config;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketHandlerImpl extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 클라이언트로부터 메시지를 받았을 때 처리하는 로직
        System.out.println("============================================================================================");
        System.out.println("Received message: " + message.getPayload());
        System.out.println("============================================================================================");
        // 클라이언트로 메시지 전송
        try {
            session.sendMessage(new TextMessage("서버에서 올바르게 전달함!"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
