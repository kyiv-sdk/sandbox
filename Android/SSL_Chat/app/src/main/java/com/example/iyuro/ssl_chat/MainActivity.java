package com.example.iyuro.ssl_chat;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.chatlibrary.chat.ChatManager;
import com.example.chatlibrary.chat.login.LoginManager;
import com.example.chatlibrary.chat.user_list.UsersListActivity;
import com.example.fingerprint.SecureLoginActivity;

public class MainActivity extends AppCompatActivity implements com.example.chatlibrary.chat.login.LoginInterface {

    public static final boolean isSSLEnabled = true;
    private final int REQUEST_APP_ENTRY_ACTIVITY = 1;
    private String username = "default";

    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callSecureLogin();
    }

    private void callSecureLogin(){
        Intent intent = new Intent(this, SecureLoginActivity.class);
        startActivityForResult(intent, REQUEST_APP_ENTRY_ACTIVITY);
    }

    private void initActivity(String ip, int port){
        setContentView(R.layout.activity_main);

        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        ChatManager.getInstance().openConnection(ip, port, isSSLEnabled, android_id);

        loginManager = new LoginManager(this);
        loginManager.logIn(this.username);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        if (requestCode == REQUEST_APP_ENTRY_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                String ip = data.getStringExtra("ip");
                int port = data.getIntExtra("port", 5454);

                this.username = data.getStringExtra("username");

                initActivity(ip, port);
            } else {
                Toast.makeText(getApplicationContext(), "Please, try again", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SecureLoginActivity.class);
                startActivityForResult(intent, REQUEST_APP_ENTRY_ACTIVITY);
            }
        }
    }

    @Override
    public void onLoginSuccess() {
        ChatManager.getInstance().setCurrentUserID(this.username);
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), UsersListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("isSSLEnabled", isSSLEnabled);
        startActivity(intent);
    }

    @Override
    public void onLoginFailed() {
        Toast.makeText(this, "Wrong log in", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionClosed() {
        Toast.makeText(this, "Connection was closed. Restart your app.", Toast.LENGTH_SHORT).show();
    }
}
