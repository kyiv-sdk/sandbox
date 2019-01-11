package com.example.iyuro.socketstest.RegisterActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.iyuro.socketstest.Messenger.MessageHandler;
import com.example.iyuro.socketstest.Messenger.MessageListener;
import com.example.iyuro.socketstest.R;
import com.example.iyuro.socketstest.UsersList.UsersListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements MessageListener {

    EditText usernameEditText;
    Button loginButton;
    public String username = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        loginButton = findViewById(R.id.sign_in_button);

        MessageHandler.getInstance().openConnection();
//        MessageHandler.getInstance().setMessageListener(this);

        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        MessageHandler.getInstance().send(android_id);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MessageHandler.getInstance().setMessageListener(this);
    }

    private void logIn(){
        username = usernameEditText.getText().toString();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("keyAction", "login");
            jsonObject.put("username", usernameEditText.getText().toString());

//            String msg = "{\"username\":\"" + usernameEditText.getText().toString() + "\",\"message\":\"login\"}";
            MessageHandler.getInstance().send(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceive(final byte[] bytesData) {
        try {
            String data ="";
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesData);

            WebResourceResponse webResourceResponse = new WebResourceResponse("text/html", "utf-8", byteArrayInputStream);

            InputStream inputStream = webResourceResponse.getData();

            StringBuilder textBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }

                data = textBuilder.toString();
            }
            Log.i("Message received:", data);

            // TODO: parse answer
            JSONObject jsonObject = new JSONObject(data);
            try {
                String keyAction = jsonObject.getString("keyAction");
                if (keyAction.equals("login")){
                    String response = jsonObject.getString("message");
                    if (response.equals("ok")){

                        saveUsername(username);

                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), UsersListActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Wrong log in", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
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
        MessageHandler.getInstance().send("exit");
        MessageHandler.getInstance().closeConnection();
    }
}

