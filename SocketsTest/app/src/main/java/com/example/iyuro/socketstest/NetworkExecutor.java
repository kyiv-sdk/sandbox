package com.example.iyuro.socketstest;

import android.os.Handler;

public class NetworkExecutor {
    int id;
    private NetworkExecutorListener networkExecutorListener;
    public native long cppStartDownloading(String request);
    public native void cppCloseDownloading(long obj);
    private long cppNetworkManager = 0;
    Handler handler;

    public NetworkExecutor(int id, NetworkExecutorListener networkExecutorListener) {
        this.id = id;
        this.networkExecutorListener = networkExecutorListener;
        handler = new Handler();
    }

    public int getId() {
        return id;
    }

    public void onSuccessDownload(final String data) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                networkExecutorListener.onDataReceive(id, data);
                closeDownloading();
            }
        });
    }

    public void startDownloading(String url){
        cppNetworkManager = cppStartDownloading(url);
    }

    public void closeDownloading(){
        cppCloseDownloading(cppNetworkManager);
    }
}
