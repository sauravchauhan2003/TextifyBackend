package com.example.demo;

import com.example.demo.Authentication.UserEntity;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebRtcHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, String> sessionUsers = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Object userObj = session.getAttributes().get("user");
        System.out.println("WebSocket connection established. User: " + userObj);
        if (userObj instanceof UserEntity user) {
            String username = user.getUsername();
            if (username != null) {
                userSessions.put(username, session);
                sessionUsers.put(session, username);
                System.out.println("WebRTC Client connected: " + username);
            } else {
                try {
                    session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        System.out.println("Received WebRTC message: " + payload);

        JSONObject json = new JSONObject(payload);
        String to = json.optString("to");
        String from = json.optString("from");
        String type = json.optString("type");
        String callType = json.optString("callType", "unknown");

        WebSocketSession receiverSession = userSessions.get(to);

        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(json.toString()));
            System.out.printf("Forwarded %s message from %s to %s [%s]%n", type, from, to, callType);
        } else {
            System.out.println("WebRTC Receiver not available or offline: " + to);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String username = sessionUsers.remove(session);
        if (username != null) {
            userSessions.remove(username);
            System.out.println("WebRTC Client disconnected: " + username);
        }
    }
}
