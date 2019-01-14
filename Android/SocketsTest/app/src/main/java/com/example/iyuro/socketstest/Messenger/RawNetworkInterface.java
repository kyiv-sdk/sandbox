package com.example.iyuro.socketstest.Messenger;

public interface RawNetworkInterface {
    void onMessageReceive(final byte[] bytesData);
}
