package com.example.chatlibrary.chat.login;
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

import com.example.chatlibrary.R;
import com.example.chatlibrary.chat.ChatManager;
import com.example.chatlibrary.chat.user_list.UsersListActivity;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoginInterface {

    private EditText usernameEditText;
    private Button loginButton;
    private String username = "default";
    private LoginManager loginManager;
    private boolean isSSLEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        loginButton = findViewById(R.id.sign_in_button);

        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Bundle bundle = getIntent().getExtras();
        isSSLEnabled = false;
        if (bundle != null){
            isSSLEnabled = bundle.getBoolean("isSSLEnabled");
        }

        ChatManager.getInstance().openConnection(isSSLEnabled, android_id);

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

