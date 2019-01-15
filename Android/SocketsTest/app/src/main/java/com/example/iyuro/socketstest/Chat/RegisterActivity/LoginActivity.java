package com.example.iyuro.socketstest.Chat.RegisterActivity;
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

import com.example.iyuro.socketstest.Chat.Messenger.NetworkManager;
import com.example.iyuro.socketstest.Chat.Messenger.NetworkInterface;
import com.example.iyuro.socketstest.R;
import com.example.iyuro.socketstest.Chat.UsersList.UsersListActivity;

import org.json.JSONException;
import org.json.JSONObject;

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

        NetworkManager.getInstance().openConnection();

        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        NetworkManager.getInstance().send(android_id);

        loginManager = new LoginManager(this);

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkManager.getInstance().send("exit");
        NetworkManager.getInstance().closeConnection();
    }

    @Override
    public void onLoginSucces() {
        saveUsername(username);
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), UsersListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLoginFailed() {
        Toast.makeText(this, "Wrong log in", Toast.LENGTH_SHORT).show();
    }
}

