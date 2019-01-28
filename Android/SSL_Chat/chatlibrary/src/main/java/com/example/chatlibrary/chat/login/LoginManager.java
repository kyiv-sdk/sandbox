package com.example.chatlibrary.chat.login;

import com.example.chatlibrary.chat.ChatMessage;
import com.example.chatlibrary.chat.MessageProtocol;
import com.example.chatlibrary.network.NetworkInterface;
import com.example.chatlibrary.network.NetworkManager;

public class LoginManager implements NetworkInterface {

    private LoginInterface loginInterface;

    public LoginManager(LoginInterface loginInterface) {
        this.loginInterface = loginInterface;
        NetworkManager.getInstance().setNetworkInterface(this);
    }

    public void logIn(String username){
        byte[] request = MessageProtocol.getInstance().createLoginRequest(username).getBytes();
        if (NetworkManager.getInstance().isConnectionOpen()) {
            NetworkManager.getInstance().send(request);
        } else {
            loginInterface.onConnectionClosed();
        }
    }

    @Override
    public void onMessageReceive(int headerLen, int fileLen, byte[] data) {
        ChatMessage chatMessage = MessageProtocol.getInstance().processReceivedMessage(headerLen, fileLen, data);
        if (chatMessage.getMessage().equals("ok")){
            loginInterface.onLoginSuccess();
        } else {
            loginInterface.onLoginFailed();
        }
    }
}
