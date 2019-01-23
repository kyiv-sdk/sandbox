package com.example.iyuro.ssl_chat.messenger;

public interface RawNetworkInterface {
    void onMessageReceive(int headerLen, int fileLen, final byte[] bytesData);
}
