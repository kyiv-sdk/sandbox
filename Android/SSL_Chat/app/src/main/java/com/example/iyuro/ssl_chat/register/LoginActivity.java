package com.example.iyuro.ssl_chat.register;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatlibrary.messenger.chat.ChatManager;
import com.example.chatlibrary.messenger.register.LoginManager;
import com.example.iyuro.ssl_chat.MainActivity;
import com.example.iyuro.ssl_chat.R;
import com.example.iyuro.ssl_chat.user_list.UsersListActivity;

import com.example.chatlibrary.messenger.register.LoginInterface;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoginInterface {

    EditText usernameEditText;
    Button loginButton;
    public String username = "default";
    LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        loginButton = findViewById(R.id.sign_in_button);

        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        ChatManager.getInstance().openConnection(MainActivity.isSSLEnabled, android_id);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });
    }

    private void logIn(){
        username = usernameEditText.getText().toString();
        loginManager.logIn(username);
    }

    private void saveUsername(String username){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.current_username_key), username);
        editor.apply();
        ChatManager.getInstance().setCurrentUserID(username);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginManager = new LoginManager(this);
    }

    @Override
    public void onLoginSuccess() {
        saveUsername(username);
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), UsersListActivity.class);
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

