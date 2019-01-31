package com.example.fingerprint;

public interface LoginInterface {
    void onLoginSuccess(String ip, String socket, String username);
    void onLoginFailed();
    void onExplainingNeed(String explaining);
}
