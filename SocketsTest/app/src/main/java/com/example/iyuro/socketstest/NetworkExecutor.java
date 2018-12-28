package com.example.iyuro.socketstest;

public class NetworkExecutor {
    private int id;
    private NetworkExecutorListener networkExecutorListener;
    private long cppNetworkManager = 0;

    public NetworkExecutor(int id, NetworkExecutorListener networkExecutorListener) {
        this.id = id;
        this.networkExecutorListener = networkExecutorListener;
    }

    public int getId() {
        return id;
    }

    public void onSuccessDownload(final String data) {
        networkExecutorListener.onDataReceive(id, data);
    }

    public void startDownloading(String url){
        cppNetworkManager = cppStartDownloading(url);
    }

    public void closeDownloading(){
        cppCloseDownloading(cppNetworkManager);
    }

    public native long cppStartDownloading(String request);
    public native void cppCloseDownloading(long obj);
}
