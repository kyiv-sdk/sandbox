package com.example.chatlibrary.network;

public interface RawNetworkInterface {
    void onMessageReceive(int headerLen, int fileLen, final byte[] bytesData);
}
