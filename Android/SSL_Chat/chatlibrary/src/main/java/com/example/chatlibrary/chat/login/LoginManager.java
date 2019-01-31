package com.example.chatlibrary.chat.login;

import android.os.Handler;

import com.example.chatlibrary.chat.network.ChatMessage;
import com.example.chatlibrary.chat.network.MessageProtocol;
import com.example.chatlibrary.chat.network.NetworkInterface;
import com.example.chatlibrary.chat.network.NetworkManager;

public class LoginManager implements NetworkInterface {

    private LoginInterface loginInterface;
    private Handler handler;

    public LoginManager(LoginInterface loginInterface) {
        this.loginInterface = loginInterface;
        NetworkManager.getInstance().setNetworkInterface(this);
        this.handler = new Handler();
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
    public void onMessageReceive(final ChatMessage chatMessage) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (chatMessage.getMessage().equals("ok")){
                    loginInterface.onLoginSuccess();
                } else {
                    loginInterface.onLoginFailed();
                }
            }
        });
    }
}
