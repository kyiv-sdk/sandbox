package com.example.iyuro.socketstest.Messenger;

public interface MessageProtocolInterface {
    String getCurrentUsername();
    String getCurrentDestionationUsername();
    void onCurrentChatNewMessage(String message);
    void onAnotherChatNewMessage(String srcID, String message);
}
