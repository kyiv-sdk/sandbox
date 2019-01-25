package com.example.mynetworklibrary.network;

public interface RawNetworkInterface {
    void onMessageReceive(int headerLen, int fileLen, final byte[] bytesData);
}
