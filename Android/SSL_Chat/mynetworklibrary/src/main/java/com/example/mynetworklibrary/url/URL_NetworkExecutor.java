package com.example.mynetworklibrary.url;

import android.os.Handler;

public class URL_NetworkExecutor {
    private int id;
    private URL_NetworkExecutorInterface URLNetworkExecutorInterface;
    private long cppNetworkManager = 0;
    private Handler handler;

    public URL_NetworkExecutor(int id, URL_NetworkExecutorInterface URLNetworkExecutorInterface) {
        this.id = id;
        this.URLNetworkExecutorInterface = URLNetworkExecutorInterface;
        this.handler = new Handler();
    }

    public int getId() {
        return id;
    }

    public void onSuccessDownload(final byte[] bytesData) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                URLNetworkExecutorInterface.onDataReceive(id, bytesData);
            }
        });
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
