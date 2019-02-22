package com.example.iyuro.ssl_chat.login;

public interface LoginInterface {
    void onLoginSuccess();
    void onLoginFailed();
    void onConnectionClosed();

    void showSignUpScreen();

    void onExplainingNeed(String explaining);
}
