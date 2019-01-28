package com.example.chatlibrary.chat;

import com.example.mynetworklibrary.chat.ChatUser;

import java.util.ArrayList;

public interface ChatInterface {
    void onNewMessage(String userID, String message);
    void onUsersListRefresh(ArrayList<ChatUser> newUsers);
}
