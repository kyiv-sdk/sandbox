package com.example.mynetworklibrary.network;

public interface NetworkInterface {
    void onMessageReceive(int headerLen, int fileLen, byte[] data);
}
