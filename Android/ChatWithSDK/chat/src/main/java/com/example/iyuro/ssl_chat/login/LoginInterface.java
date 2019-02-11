package com.example.iyuro.ssl_chat.login;

public interface LoginInterface {
    void onLoginSuccess();
    void onSignUpFailed();
    void onConnectionClosed();
    void onDeviceNotSecure();

    void showSignUpScreen();
    void showAuthScreen();

    void onExplainingNeed(String explaining);
}
