package com.example.iyuro.ssl_chat.login;

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

import com.example.authentication.AuthActivity;
import com.example.iyuro.ssl_chat.R;
import com.example.iyuro.ssl_chat.user_list.UsersListActivity;

public class LoginActivity extends AppCompatActivity implements LoginInterface {

    private final int DEVICE_CREDENTIAL_REQUEST_ID = 1;

    private EditText mIpEditText, mPortEditText, mUsernameEditText;
    private Button signUpBtn;
    private LinearLayout fingerprint_layout;
    private TextView hintText;

    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginManager = new LoginManager(this, this);
        loginManager.prepareLogIn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == DEVICE_CREDENTIAL_REQUEST_ID) {
            loginManager.signIn(resultCode);
        }
    }

    @Override
    public void onLoginSuccess() {
        Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), UsersListActivity.class);
        intent.putExtra("isSSLEnabled", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onConnectionClosed() {
        Toast.makeText(this, "Connection was closed. Restart your app.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeviceNotSecure() {
        setContentView(R.layout.activity_login);

        fingerprint_layout = findViewById(R.id.fingerprint_layout);
        fingerprint_layout.setVisibility(View.VISIBLE);

        hintText = findViewById(R.id.hint_text);
        hintText.setVisibility(View.VISIBLE);
        hintText.setText("User is not authenticated.\nYou should set password on your phone to work with this application");
    }

    @Override
    public void showSignUpScreen(){
        setContentView(R.layout.activity_login);

        mIpEditText = findViewById(R.id.secure_activity_ip_edittext);
        mPortEditText = findViewById(R.id.secure_activity_port_edittext);
        mUsernameEditText = findViewById(R.id.secure_activity_username_edittext);

        fingerprint_layout = findViewById(R.id.fingerprint_layout);
        hintText = findViewById(R.id.hint_text);

        signUpBtn = findViewById(R.id.secure_activity_sign_up_button);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = mIpEditText.getText().toString();
                String port = mPortEditText.getText().toString();
                String username = mUsernameEditText.getText().toString();
                loginManager.signUp(ip, port, username);
                signUpBtn.setEnabled(false);
            }
        });

        mIpEditText.setVisibility(View.VISIBLE);
        mPortEditText.setVisibility(View.VISIBLE);
        mUsernameEditText.setVisibility(View.VISIBLE);
        signUpBtn.setVisibility(View.VISIBLE);
        hintText.setVisibility(View.VISIBLE);
        fingerprint_layout.setVisibility(View.GONE);
    }

    @Override
    public void onSignUpFailed() {
        signUpBtn.setEnabled(true);
    }

    @Override
    public void showAuthScreen() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivityForResult(intent, DEVICE_CREDENTIAL_REQUEST_ID);
    }

    @Override
    public void onExplainingNeed(String explaining) {
        Toast.makeText(this, explaining, Toast.LENGTH_LONG).show();
    }
}
