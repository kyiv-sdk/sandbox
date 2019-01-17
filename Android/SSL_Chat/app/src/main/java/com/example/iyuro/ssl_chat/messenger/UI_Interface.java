package com.example.iyuro.ssl_chat.messenger;

public interface UI_Interface {
    void onUsersListRefresh();
    void onNewMessage(String srcID, String message);
}
