package com.example.demo;

import jakarta.persistence.Embeddable;
import org.springframework.stereotype.Component;

@Component
@Embeddable
public class Message {
    private String sender;
    private String receiver;
    private String message;

    // No-arg constructor (required for Jackson)
    public Message() {
    }

    // All-args constructor (optional for convenience)
    public Message(String sender, String receiver, String message) {

        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }



    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
