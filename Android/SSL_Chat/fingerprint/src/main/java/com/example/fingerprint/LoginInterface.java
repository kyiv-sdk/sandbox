package com.example.fingerprint;

public interface LoginInterface {
    void onLoginSuccess();
    void onAuthSucceeded(String password);
    void onExplainingNeed(String explaining);
}
