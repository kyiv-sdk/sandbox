package com.example.iyuro.socketstest.Messenger;

public interface UI_Interface {
    void onUsersListRefresh();
    void onNewMessage(String srcID, String message);
}
