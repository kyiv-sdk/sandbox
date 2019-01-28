package com.example.mynetworklibrary.chat;

public interface NativeNetworkInterface {
    void onMessageReceive(int headerLen, int fileLen, byte[] data);
}
