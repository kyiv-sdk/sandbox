package com.example.iyuro.socketstest.Messenger;

public class UserMessage {
    String message;
    String sender; // if 1 -> current, if 2 -> server

    public UserMessage(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
