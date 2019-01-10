package com.example.iyuro.socketstest.URL;

public interface NetworkExecutorListener {
    void onDataReceive(int NetworkExecutorId, final byte[] bytesData);
}
