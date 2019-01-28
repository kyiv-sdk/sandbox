package com.example.browser.url;

public interface NetworkExecutorListener {
    void onDataReceive(int NetworkExecutorId, final byte[] bytesData);
}
