package com.example.chatlibrary.messenger.network;

import android.os.Handler;
import android.util.Log;

import com.example.chatlibrary.messenger.chat.MessageProtocol;
import com.example.mynetworklibrary.network.NativeNetworkInterface;
import com.example.mynetworklibrary.network.NativeNetworkManager;


public class NetworkManager implements NativeNetworkInterface {

    private static final NetworkManager ourInstance = new NetworkManager();
    private NetworkInterface networkInterface;
    private Handler handler;

    public NetworkManager() {
        this.handler = new Handler();
        NativeNetworkManager.getInstance().setNativeNetworkInterface(this);
    }

    public static NetworkManager getInstance() {
        return ourInstance;
    }

    public void setNetworkInterface(NetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
    }

    public void send(final byte[] bytesData){
        NativeNetworkManager.getInstance().send(bytesData);
    }

    public void openConnection(boolean isSSLEnabled, String uniqueID){
        NativeNetworkManager.getInstance().openConnection(isSSLEnabled);
        byte[] uniquIdRequest = MessageProtocol.getInstance().createUniqueIDRequest(uniqueID).getBytes();
        NativeNetworkManager.getInstance().send(uniquIdRequest);
    }

    public boolean isConnectionOpen(){
        return !NativeNetworkManager.getInstance().isConnectionClosed();
    }

    public void closeConnection(){
        NativeNetworkManager.getInstance().closeConnection();
    }

    @Override
    public void onMessageReceive(final int headerLen, final int fileLen, final byte[] bytesData) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (bytesData.length > 0) {
                    networkInterface.onMessageReceive(headerLen, fileLen, bytesData);
                } else {
                    Log.i("--------MY_LOG--------", "closing connection");
                    closeConnection();
                }
            }
        });
    }
}
