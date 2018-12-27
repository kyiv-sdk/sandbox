package com.example.iyuro.socketstest;

public interface NetworkExecutorListener {
    void onDataReceive(int NetworkExecutorId, final String data);
}
