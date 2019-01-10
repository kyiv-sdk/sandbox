package com.example.iyuro.socketstest.Messenger;

public class UserMessage {
    String message;
    int sender; // if 1 -> current, if 2 -> server

    public UserMessage(String message, int sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }
}
