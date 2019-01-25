package com.example.chatlibrary.messenger.network;

public interface NetworkInterface {
    void onMessageReceive(int headerLen, int fileLen, byte[] data);
}
