package com.example.mynetworklibrary.network;

public interface NativeNetworkInterface {
    void onMessageReceive(int headerLen, int fileLen, byte[] data);
}
