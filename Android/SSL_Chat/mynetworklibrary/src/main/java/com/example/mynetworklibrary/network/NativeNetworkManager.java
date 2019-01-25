package com.example.mynetworklibrary.network;

public class NativeNetworkManager implements RawNetworkInterface {
    static {
        System.loadLibrary("native-lib");
    }

    private int BASIC_PORT = 4545;
    private int SSL_PORT = 5454;

    private static final NativeNetworkManager ourInstance = new NativeNetworkManager();
    private NativeNetworkInterface nativeNetworkInterface;
    private long cppMessageHandler;

    public NativeNetworkManager() {
        this.cppMessageHandler = -1;
    }

    public static NativeNetworkManager getInstance() {
        return ourInstance;
    }

    public void setNativeNetworkInterface(NativeNetworkInterface nativeNetworkInterface) {
        this.nativeNetworkInterface = nativeNetworkInterface;
    }

    public int getBASIC_PORT() {
        return BASIC_PORT;
    }

    public void setBASIC_PORT(int BASIC_POST) {
        this.BASIC_PORT = BASIC_POST;
    }

    public int getSSL_PORT() {
        return SSL_PORT;
    }

    public void setSSL_PORT(int SSL_PORT) {
        this.SSL_PORT = SSL_PORT;
    }

    public void send(final byte[] bytesData){
        if (cppMessageHandler != -1) {
            cppSendMessage(cppMessageHandler, bytesData);
        }
    }

    public void openConnection(boolean isSSLEnabled){
        if (cppMessageHandler == -1) {
            this.cppMessageHandler = cppCreateMessageHandler("10.129.171.8", isSSLEnabled? SSL_PORT : BASIC_PORT, isSSLEnabled);
        }
    }

    public void openConnection(String hostName, boolean isSSLEnabled){
        if (cppMessageHandler == -1) {
            this.cppMessageHandler = cppCreateMessageHandler(hostName, isSSLEnabled? SSL_PORT : BASIC_PORT, isSSLEnabled);
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
            nativeNetworkInterface.onMessageReceive(headerLen, fileLen, bytesData);
        }
    }

    private native long cppCreateMessageHandler(String host, int port, boolean isSSLEnabled);
    private native void cppSendMessage(long connection, byte[] bytesData);
    private native void cppDeleteMessageHandler(long obj);
    private native boolean cppIsConnectionClosed(long obj);
}
