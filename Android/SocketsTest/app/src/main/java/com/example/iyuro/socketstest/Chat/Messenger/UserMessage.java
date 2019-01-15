package com.example.iyuro.socketstest.Chat.Messenger;

public class UserMessage {
    private String message;
    private boolean sender; // if 0 -> current, if 1 -> server

    public UserMessage(String message, Boolean sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getSender() {
        return sender;
    }

    public void setSender(boolean sender) {
        this.sender = sender;
    }
}
