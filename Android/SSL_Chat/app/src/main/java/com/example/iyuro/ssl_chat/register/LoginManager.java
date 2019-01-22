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
        String request = MessageProtocol.getInstance().createLoginRequest(username).toString();
        NetworkManager.getInstance().send(request.getBytes());
    }

    @Override
    public void onMessageReceive(String data) {
        ChatMessage chatMessage = MessageProtocol.getInstance().processReceivedMessage(data);
        if (chatMessage.getMessage().equals("ok")){
            loginInterface.onLoginSuccess();
        } else {
            loginInterface.onLoginFailed();
        }
    }
}
