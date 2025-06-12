package com.example.demo;

import com.example.demo.Authentication.UserEntity;
import com.example.demo.Authentication.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SimpleWebSocketHandler extends TextWebSocketHandler {

    private final UserRepository userRepository;
    private final SavedMessagesRepository savedMessagesRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<WebSocketSession, String> usernames = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    public SimpleWebSocketHandler(UserRepository userRepository, SavedMessagesRepository savedMessagesRepository) {
        this.userRepository = userRepository;
        this.savedMessagesRepository = savedMessagesRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Object userObj = session.getAttributes().get("user");
        if (userObj instanceof UserEntity user) {
            String username = user.getUsername();
            if (username != null) {
                userSessions.put(username, session);
                usernames.put(session, username);

                // Notify all active users about the new connection
                String connectMessage = objectMapper.writeValueAsString(
                        Map.of("type", "connect", "user", username)
                );
                for (WebSocketSession activeSession : userSessions.values()) {
                    if (activeSession.isOpen()) {
                        activeSession.sendMessage(new TextMessage(connectMessage));
                    }
                }

                // Send all registered users to the newly connected client
                List<String> allUsers = userRepository.findAllUsernames();
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                        Map.of("type", "allUsers", "users", allUsers)
                )));

                // Send currently active users to the newly connected client
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                        Map.of("type", "activeUsers", "users", userSessions.keySet())
                )));

                // Send any undelivered messages
                List<SavedMessages> savedMessages = savedMessagesRepository.findByReceivernameAndReceivedFalse(username);
                for (SavedMessages s : savedMessages) {
                    Message m = s.getMessage();
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                            Map.of("type", "message", "data", m)
                    )));
                    savedMessagesRepository.markMessageAsReceived(s.getId());
                }
                savedMessagesRepository.deleteAllReceivedMessages();
            } else {
                session.close();
            }
        } else {
            session.close();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Message newMessage = objectMapper.readValue(message.getPayload(), Message.class);
        WebSocketSession receiverSession = userSessions.get(newMessage.getReceiver());

        if (receiverSession != null) {
            // Send to recipient
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                    Map.of("type", "message", "data", newMessage)
            )));
        } else {
            // If receiver not found in DB
            if (userRepository.findByUsername(newMessage.getReceiver()).isEmpty()) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                        Map.of("type", "error", "message", "User Not Found")
                )));
            } else {
                // Save for later delivery
                SavedMessages x = new SavedMessages();
                x.setMessage(newMessage);
                x.setReceivername(newMessage.getReceiver());
                x.setReceived(false);
                savedMessagesRepository.save(x);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = usernames.remove(session);
        if (username != null) {
            userSessions.remove(username);

            // Notify all users about disconnection
            String disconnectMessage = objectMapper.writeValueAsString(
                    Map.of("type", "disconnect", "user", username)
            );
            for (WebSocketSession activeSession : userSessions.values()) {
                if (activeSession.isOpen()) {
                    activeSession.sendMessage(new TextMessage(disconnectMessage));
                }
            }
        }
    }
}
