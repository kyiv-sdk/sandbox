package com.example.iyuro.ssl_chat.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.iyuro.ssl_chat.R;
import com.example.iyuro.ssl_chat.user_list.UsersListActivity;
import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.widget.GDEditText;

import java.util.Map;

public class LoginActivity extends Activity implements LoginInterface, GDStateListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private GDEditText mIpEditText, mPortEditText, mUsernameEditText;
    private Button signUpBtn;

    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GDAndroid.getInstance().activityInit(this);

        loginManager = new LoginManager(this, this);
        loginManager.prepareLogIn();
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
    public void showSignUpScreen(){
        setContentView(R.layout.my_activity_login);

        mIpEditText = findViewById(R.id.secure_activity_ip_edittext);
        mPortEditText = findViewById(R.id.secure_activity_port_edittext);
        mUsernameEditText = findViewById(R.id.secure_activity_username_edittext);

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
    }

    @Override
    public void onLoginFailed() {
        signUpBtn.setEnabled(true);
    }

    @Override
    public void onExplainingNeed(String explaining) {
        Toast.makeText(this, explaining, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthorized() {
        //If Activity specific GDStateListener is set then its onAuthorized( ) method is called when
        //the activity is started if the App is already authorized
        Log.i(TAG, "onAuthorized()");
    }

    @Override
    public void onLocked() {
        Log.i(TAG, "onLocked()");
    }

    @Override
    public void onWiped() {
        Log.i(TAG, "onWiped()");
    }

    @Override
    public void onUpdateConfig(Map<String, Object> settings) {
        Log.i(TAG, "onUpdateConfig()");
    }

    @Override
    public void onUpdatePolicy(Map<String, Object> policyValues) {
        Log.i(TAG, "onUpdatePolicy()");
    }

    @Override
    public void onUpdateServices() {
        Log.i(TAG, "onUpdateServices()");
    }

    @Override
    public void onUpdateEntitlements() {
        Log.i(TAG, "onUpdateEntitlements()");
    }
}
