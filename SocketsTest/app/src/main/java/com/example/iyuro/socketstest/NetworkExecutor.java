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

    public void onSuccessDownload(final byte[] bytesData) {
        networkExecutorListener.onDataReceive(id, bytesData);
    }

    public void startDownloading(String protocol, String host, int port){
        cppNetworkManager = cppStartDownloading(protocol, host, port);
    }

    public void closeDownloading(){
        cppCloseDownloading(cppNetworkManager);
    }

    public native long cppStartDownloading(String protocol, String host, int port);
    public native void cppCloseDownloading(long obj);
}
