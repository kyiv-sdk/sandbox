package com.example.iyuro.ssl_chat.messenger;

public interface RawNetworkInterface {
    void onMessageReceive(final byte[] bytesData);
}
