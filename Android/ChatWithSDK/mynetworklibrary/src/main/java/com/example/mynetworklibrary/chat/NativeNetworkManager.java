package com.example.mynetworklibrary.chat;

import android.os.Handler;

public class NativeNetworkManager implements RawNetworkInterface {
    static {
        System.loadLibrary("native-lib");
    }

    private static final NativeNetworkManager ourInstance = new NativeNetworkManager();
    private NativeNetworkInterface nativeNetworkInterface;
    private long cppMessageHandler;
    private Handler handler;

    public NativeNetworkManager() {
        this.cppMessageHandler = -1;
        this.handler = new Handler();
    }

    public static NativeNetworkManager getInstance() {
        return ourInstance;
    }

    public void setNativeNetworkInterface(NativeNetworkInterface nativeNetworkInterface) {
        this.nativeNetworkInterface = nativeNetworkInterface;
    }

    public void send(final byte[] bytesData){
        if (cppMessageHandler != -1) {
            cppSendMessage(cppMessageHandler, bytesData);
        }
    }

    public void openConnection(String ip, int port, boolean isSSLEnabled){
        if (cppMessageHandler == -1) {
            this.cppMessageHandler = cppCreateMessageHandler(ip, port, isSSLEnabled);
        }
    }

    public boolean isConnectionClosed(){
        return cppMessageHandler == -1 || cppIsConnectionClosed(cppMessageHandler);
    }

    public void closeConnection(){
        if (cppMessageHandler != -1) {
            cppDeleteMessageHandler(cppMessageHandler);
            cppMessageHandler = -1;
        }
    }

    @Override
    public void onMessageReceive(final int headerLen, final int fileLen, final byte[] bytesData) {
        if (bytesData == null){
            cppMessageHandler = -1;
        } else {
            if (bytesData.length > 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        nativeNetworkInterface.onMessageReceive(headerLen, fileLen, bytesData);
                    }
                });
            }
        }
    }

    private native long cppCreateMessageHandler(String host, int port, boolean isSSLEnabled);
    private native void cppSendMessage(long connection, byte[] bytesData);
    private native void cppDeleteMessageHandler(long obj);
    private native boolean cppIsConnectionClosed(long obj);
}
