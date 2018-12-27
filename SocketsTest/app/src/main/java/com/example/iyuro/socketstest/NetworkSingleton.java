package com.example.iyuro.socketstest;

import android.os.Bundle;
import android.os.Message;

public class NetworkSingleton {
    private static final NetworkSingleton ourInstance = new NetworkSingleton();
    private MyHandler myHandler;
    public native long startDownload(String request);
    public native void endDownload(long obj);

    private long cppNetworker = 0;

    public void setNetworkDataListener(NetworkDataListener networkDataListener) {
        myHandler = new MyHandler(networkDataListener);
    }

    public static NetworkSingleton getInstance() {
        return ourInstance;
    }

    private NetworkSingleton() {
    }

    public void download(String url){
        cppNetworker = startDownload(url);
    }

    public void endDownloading(){
        endDownload(cppNetworker);
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
            if (networkDataListener != null) networkDataListener.OnDataReceive(data);
        }
    }
}
