package com.example.iyuro.socketstest.chat.messenger;

public interface UI_Interface {
    void onUsersListRefresh();
    void onNewMessage(String srcID, String message);
}
