package com.example.browser.url;

public interface NetworkExecutorInterface {
    void onDataReceive(int NetworkExecutorId, final byte[] bytesData);
}
