package com.example.chatlibrary.network;

public interface NativeNetworkInterface {
    void onMessageReceive(int headerLen, int fileLen, byte[] data);
}
