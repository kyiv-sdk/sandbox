package com.example.iyuro.socketstest.url;

public interface NetworkExecutorListener {
    void onDataReceive(int NetworkExecutorId, final byte[] bytesData);
}
