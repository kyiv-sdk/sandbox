package com.good.mygoodsample.network;

public interface NetworkInterface {
    void onMessageReceive(ChatMessage chatMessage);
    void closeConnection();
}
