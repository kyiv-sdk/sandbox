package com.example.browser.url;

public class NetworkExecutor {
    private int id;
    private NetworkExecutorInterface networkExecutorInterface;
    private long cppNetworkManager = 0;

    public NetworkExecutor(int id, NetworkExecutorInterface networkExecutorInterface) {
        this.id = id;
        this.networkExecutorInterface = networkExecutorInterface;
    }

    public int getId() {
        return id;
    }

    public void onSuccessDownload(final byte[] bytesData) {
        networkExecutorInterface.onDataReceive(id, bytesData);
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
