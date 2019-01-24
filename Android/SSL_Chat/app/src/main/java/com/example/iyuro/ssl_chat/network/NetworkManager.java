package com.example.iyuro.ssl_chat.network;

import android.os.Handler;
import android.util.Log;

import com.example.iyuro.ssl_chat.MainActivity;
import com.example.iyuro.ssl_chat.messenger.ChatMessage;
import com.example.iyuro.ssl_chat.messenger.MessageProtocol;
import com.example.iyuro.ssl_chat.messenger.RawNetworkInterface;

public class NetworkManager implements RawNetworkInterface {
    static {
        System.loadLibrary("native-lib");
    }

    private final int BASIC_POST = 4545;
    private final int SSL_PORT = 5454;

    private static final NetworkManager ourInstance = new NetworkManager();
    private NetworkInterface networkInterface;
    private Handler handler;
    private long cppMessageHandler;

    public NetworkManager() {
        this.handler = new Handler();
        this.cppMessageHandler = -1;
    }

    public static NetworkManager getInstance() {
        return ourInstance;
    }

    public void setNetworkInterface(NetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
    }

    public void send(final byte[] bytesData){
        cppSendMessage(cppMessageHandler, bytesData);
    }

    public void openConnection(String uniqueID){
        if (cppMessageHandler == -1) {
            this.cppMessageHandler = cppCreateMessageHandler("10.129.171.8", MainActivity.isSSLEnabled? SSL_PORT : BASIC_POST, MainActivity.isSSLEnabled);
            Log.i("--------MY_LOG--------", uniqueID);
            ChatMessage uniqueIDRequest = MessageProtocol.getInstance().createUniqueIDRequest(uniqueID);
            this.send(uniqueIDRequest.getBytes());
        }
    }

    public boolean isConnectionClosed(){
        return cppMessageHandler == -1;
    }

    public void closeConnection(){
        cppDeleteMessageHandler(cppMessageHandler);
        cppMessageHandler = -1;
    }

    @Override
    public void onMessageReceive(int headerLen, int fileLen, final byte[] bytesData) {
        if (bytesData.length > 0) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.i("--------MY_LOG--------", "post runned");
                    networkInterface.onMessageReceive(headerLen, fileLen, bytesData);
                }
            });
        } else {
            closeConnection();
        }
    }

    private native long cppCreateMessageHandler(String host, int port, boolean isSSLEnabled);
    private native void cppSendMessage(long connection, byte[] bytesData);
    private native void cppDeleteMessageHandler(long obj);
}
