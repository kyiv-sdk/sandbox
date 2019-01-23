package com.example.iyuro.ssl_chat.register;

import com.example.iyuro.ssl_chat.messenger.ChatMessage;
import com.example.iyuro.ssl_chat.messenger.MessageProtocol;
import com.example.iyuro.ssl_chat.network.NetworkInterface;
import com.example.iyuro.ssl_chat.network.NetworkManager;

public class LoginManager implements NetworkInterface {

    private LoginInterface loginInterface;

    public LoginManager(LoginInterface loginInterface) {
        this.loginInterface = loginInterface;
        NetworkManager.getInstance().setNetworkInterface(this);
    }

    public void logIn(String username){
        byte[] request = MessageProtocol.getInstance().createLoginRequest(username).getBytes();
        NetworkManager.getInstance().send(request);
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
