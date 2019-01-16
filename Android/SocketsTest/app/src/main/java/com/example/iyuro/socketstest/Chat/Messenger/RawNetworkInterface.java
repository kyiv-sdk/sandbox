package com.example.iyuro.socketstest.chat.messenger;

public interface RawNetworkInterface {
    void onMessageReceive(final byte[] bytesData);
}
