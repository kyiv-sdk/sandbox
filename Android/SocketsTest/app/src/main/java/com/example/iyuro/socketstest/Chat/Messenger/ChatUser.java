package com.example.iyuro.socketstest.Chat.Messenger;

import java.util.ArrayList;
import java.util.Objects;

public class ChatUser {
    private String userID;
    private ArrayList<UserMessage> messageArrayList;
    private int unreadMessagesCounter;

    public ChatUser(String userID) {
        this.userID = userID;
        this.messageArrayList = new ArrayList<>();
        this.unreadMessagesCounter = 0;
    }

    public void addMessage(UserMessage message){
        messageArrayList.add(message);
        unreadMessagesCounter++;
    }

    public String getUserID() {
        return userID;
    }

    public ArrayList<UserMessage> getMessageArrayList() {
        return messageArrayList;
    }

    public void resetUnreadMessagesCounter() {
        this.unreadMessagesCounter = 0;
    }

    public int getUnreadMessagesCounter() {
        return unreadMessagesCounter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatUser chatUser = (ChatUser) o;
        return Objects.equals(userID, chatUser.userID);
    }
}
