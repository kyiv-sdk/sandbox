package com.example.iyuro.ssl_chat.network;

public interface NetworkInterface {
    void onMessageReceive(int headerLen, int fileLen, byte[] data);
}
