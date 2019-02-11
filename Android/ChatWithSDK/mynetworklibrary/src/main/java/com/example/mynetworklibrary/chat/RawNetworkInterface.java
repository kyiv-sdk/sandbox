package com.example.mynetworklibrary.chat;

public interface RawNetworkInterface {
    void onMessageReceive(int headerLen, int fileLen, final byte[] bytesData);
}
