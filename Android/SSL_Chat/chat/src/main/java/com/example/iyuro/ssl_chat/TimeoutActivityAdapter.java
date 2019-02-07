package com.example.iyuro.ssl_chat;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.authentication.AuthActivity;
import com.example.timeout.TimeoutActivity;

public class TimeoutActivityAdapter extends TimeoutActivity {
    private final int DEVICE_CREDENTIAL_REQUEST_ID = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == DEVICE_CREDENTIAL_REQUEST_ID) {
            if (resultCode != RESULT_OK){
                showAuthScreen();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onTimeOut() {
        super.onTimeOut();
        showAuthScreen();
    }

    private void showAuthScreen() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivityForResult(intent, DEVICE_CREDENTIAL_REQUEST_ID);
    }
}
