package com.example.mynetworklibrary.chat;

public interface NetworkInterface {
    void onMessageReceive(int headerLen, int fileLen, byte[] data);
}
