package com.example.mynetworklibrary.messenger;

public interface RawNetworkInterface {
    void onMessageReceive(int headerLen, int fileLen, final byte[] bytesData);
}
