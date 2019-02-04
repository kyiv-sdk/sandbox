package com.example.authentication;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AuthActivity extends AppCompatActivity implements AuthInterface{

    private final int DEVICE_CREDENTIAL_REQUEST_ID = 1;

    private AuthManager authManager = AuthManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_auth);

        authManager.setAuthInterface(this);

        authManager.startAuth();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == DEVICE_CREDENTIAL_REQUEST_ID) {
            authManager.manageAuthResult(resultCode);
        }
    }

    @Override
    public void showAuthScreen() {
        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        Intent intent = keyguardManager.createConfirmDeviceCredentialIntent("Please, log in", "You can use password or fingerprint");
        startActivityForResult(intent, DEVICE_CREDENTIAL_REQUEST_ID);
    }

    @Override
    public void onAuthSucceed() {
        setResult(RESULT_OK);
        finish();
    }
}
