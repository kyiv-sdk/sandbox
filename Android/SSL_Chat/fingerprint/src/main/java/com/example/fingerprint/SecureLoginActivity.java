package com.example.fingerprint;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SecureLoginActivity extends AppCompatActivity implements LoginInterface {

    private final int DEVICE_CREDENTIAL_REQUEST_ID = 1;

    private EditText mIpEditText, mSocketEditText ,mUsernameEditText;
    private Button signUpBtn, usePasswordBtn;
    private LinearLayout fingerprint_layout;
    private TextView hintText;

    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_login);

        mIpEditText = findViewById(R.id.secure_activity_ip_edittext);
        mSocketEditText = findViewById(R.id.secure_activity_socket_edittext);
        mUsernameEditText = findViewById(R.id.secure_activity_username_edittext);

        usePasswordBtn = findViewById(R.id.secure_activity_use_password_button);
        signUpBtn = findViewById(R.id.secure_activity_sign_up_button);

        fingerprint_layout = findViewById(R.id.fingerprint_layout);
        hintText = findViewById(R.id.hint_text);

        loginManager = new LoginManager(this, this);
        if (!loginManager.isUserAuthenticated()){
            hintText.setVisibility(View.VISIBLE);
            hintText.setText("User is not authenticated.\nYou should set password on your phone to work with this application");
            return;
        }

        if (!loginManager.isAlreadySignedUp()){
            usePasswordBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    useDeviceCredentials();
                }
            });
            signUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String ip = mIpEditText.getText().toString();
                    String socket = mSocketEditText.getText().toString();
                    String username = mUsernameEditText.getText().toString();
                    if (ip.length() > 0 && socket.length() > 0 && username.length() > 0) {
                        loginManager.prepareSignUp(ip, socket, username);
                    }
                }
            });

            mIpEditText.setVisibility(View.VISIBLE);
            mSocketEditText.setVisibility(View.VISIBLE);
            mUsernameEditText.setVisibility(View.VISIBLE);
            signUpBtn.setVisibility(View.VISIBLE);
            hintText.setVisibility(View.VISIBLE);
            usePasswordBtn.setVisibility(View.GONE);
            fingerprint_layout.setVisibility(View.GONE);
        } else {
            useDeviceCredentials();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == DEVICE_CREDENTIAL_REQUEST_ID)
        {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                loginManager.onAuthSucceeded();
            } else {
                useDeviceCredentials();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginManager.close();
    }

    @Override
    public void onBackPressed() {}

    @Override
    public void onLoginSuccess(String ip, String port, String username) {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        intent.putExtra("ip", ip);
        intent.putExtra("port", Integer.parseInt(port));
        intent.putExtra("username", username);
        finish();
    }

    @Override
    public void onLoginFailed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void onExplainingNeed(String explaining) {
        Toast.makeText(this, explaining, Toast.LENGTH_LONG).show();
    }

    private void useDeviceCredentials(){
        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        Intent intent = keyguardManager.createConfirmDeviceCredentialIntent("Please, log in", "You can use password or fingerprint");
        startActivityForResult(intent, DEVICE_CREDENTIAL_REQUEST_ID);
    }
}
