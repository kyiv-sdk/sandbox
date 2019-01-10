package com.example.iyuro.socketstest.Messenger;

public interface MessageListener {
    void onMessageReceive(final byte[] bytesData);
}
