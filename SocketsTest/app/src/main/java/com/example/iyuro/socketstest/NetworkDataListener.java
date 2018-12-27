package com.example.iyuro.socketstest;

public interface NetworkDataListener {
    void onDataReceive(final int id, final String data);
}
