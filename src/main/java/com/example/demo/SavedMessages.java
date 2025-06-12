package com.example.demo;

import jakarta.persistence.*;

@Entity
@Table(name = "SavedMessages")
public class SavedMessages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String receivername;

    private boolean received = false;

    @Embedded
    private Message message;

    // Constructors
    public SavedMessages() {
    }

    public SavedMessages(String receivername, Message message) {
        this.receivername = receivername;
        this.message = message;
        this.received = false;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getReceivername() {
        return receivername;
    }

    public void setReceivername(String receivername) {
        this.receivername = receivername;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
