package com.example.mynetworklibrary.url;

import android.os.Handler;

public class URL_NetworkExecutor {
    protected int id;
    protected URL_NetworkExecutorInterface URLNetworkExecutorInterface;
    protected long cppNetworkManager = 0;
    protected Handler handler;

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
                URLNetworkExecutorInterface.onDataReceive(id, bytesData, true);
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
