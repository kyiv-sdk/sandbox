package com.example.iyuro.socketstest;

import android.os.Bundle;
import android.os.Message;
import android.widget.TextView;

import java.util.logging.Handler;

public class NetworkSingleton {
    public native void makeRequest(String request);

    private static final NetworkSingleton ourInstance = new NetworkSingleton();
    private MyHandler myHandler;

    public void setNetworkDataListener(NetworkDataListener networkDataListener) {
        myHandler = new MyHandler(networkDataListener);
    }

    public static NetworkSingleton getInstance() {
        return ourInstance;
    }

    private NetworkSingleton() {
    }

    public void download(String url){
        makeRequest(url);
    }

    public void onSuccessDownload(String data){
        Message message = myHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("DATA", data);
        message.setData(bundle);
        myHandler.sendMessage(message);
    }

    public static class MyHandler extends android.os.Handler {
        NetworkDataListener networkDataListener;

        public MyHandler(NetworkDataListener networkDataListener) {
            this.networkDataListener = networkDataListener;
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String data = bundle.getString("DATA");
            networkDataListener.OnDataReceive(data);
        }
    }
}
