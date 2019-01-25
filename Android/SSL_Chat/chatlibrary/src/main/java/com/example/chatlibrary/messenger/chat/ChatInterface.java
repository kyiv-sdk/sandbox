package com.example.chatlibrary.messenger.chat;

import java.util.ArrayList;

public interface ChatInterface {
    void onNewMessage(String userID, String message);
    void onUsersListRefresh(ArrayList<ChatUser> newUsers);
}