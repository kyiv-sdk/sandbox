package com.example.iyuro.ssl_chat.login;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iyuro.ssl_chat.R;
import com.example.iyuro.ssl_chat.user_list.UsersListActivity;
import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;

import java.util.Map;

public class LoginActivity extends Activity implements LoginInterface, GDStateListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText mIpEditText, mPortEditText, mUsernameEditText;
    private Button signUpBtn;
    private LinearLayout fingerprint_layout;
    private TextView hintText;

    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GDAndroid.getInstance().activityInit(this);

        loginManager = new LoginManager(this, this);
        loginManager.prepareLogIn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
////            loginManager.signIn(resultCode);
//            if (resultCode == RESULT_OK){
//                loginManager = new LoginManager(this, this);
//                loginManager.prepareLogIn();
//            }
//        }
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
        setContentView(R.layout.my_activity_login);

        fingerprint_layout = findViewById(R.id.fingerprint_layout);
        fingerprint_layout.setVisibility(View.VISIBLE);

        hintText = findViewById(R.id.hint_text);
        hintText.setVisibility(View.VISIBLE);
        hintText.setText("User is not authenticated.\nYou should set password on your phone to work with this application");
    }

    @Override
    public void showSignUpScreen(){
        setContentView(R.layout.my_activity_login);

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
//        Intent intent = new Intent(this, AuthActivity.class);
//        startActivityForResult(intent, DEVICE_CREDENTIAL_REQUEST_ID);
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
