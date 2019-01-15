package com.example.iyuro.socketstest.Chat.Messenger;

public interface UI_Interface {
    void onUsersListRefresh();
    void onNewMessage(String srcID, String message);
}
