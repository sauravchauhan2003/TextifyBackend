package com.example.TextifyBackend.MessageService;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageService {
    private Map<String, List<Message>> userMessageQueues = new ConcurrentHashMap<>();
    public void sendMessage(Message newMessage) {
        userMessageQueues.putIfAbsent(newMessage.getReceiver(), new ArrayList<>());
        userMessageQueues.get(newMessage.getReceiver()).add(newMessage);
        System.out.println("File sent: " + newMessage);
    }
    public List<Message> receiveMessage(String username) {
        List<Message> userMessages = userMessageQueues.getOrDefault(username, new ArrayList<>());
        userMessageQueues.put(username, new ArrayList<>()); // Clear after receiving
        return userMessages;
    }
}
