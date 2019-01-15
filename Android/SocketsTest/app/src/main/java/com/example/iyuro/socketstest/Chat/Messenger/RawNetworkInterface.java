package com.example.iyuro.socketstest.Chat.Messenger;

public interface RawNetworkInterface {
    void onMessageReceive(final byte[] bytesData);
}
