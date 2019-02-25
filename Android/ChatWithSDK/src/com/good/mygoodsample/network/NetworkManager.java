package com.good.mygoodsample.network;

import android.util.Log;

import com.example.mynetworklibrary.chat.NativeNetworkInterface;
import com.example.mynetworklibrary.chat.NativeNetworkManager;

public class NetworkManager implements NativeNetworkInterface {

    private static final NetworkManager ourInstance = new NetworkManager();
    private NetworkInterface networkInterface;

    public NetworkManager() {

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

    public void openConnection(String ip, int port, boolean isSSLEnabled, String uniqueID){
        NativeNetworkManager.getInstance().openConnection(ip, port, isSSLEnabled);
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
        ChatMessage chatMessage = MessageProtocol.getInstance().processReceivedMessage(headerLen, fileLen, bytesData);
        if (bytesData.length > 0) {
            networkInterface.onMessageReceive(chatMessage);
        } else {
            Log.i("--------MY_LOG--------", "closing connection");
            closeConnection();
        }
    }
}
