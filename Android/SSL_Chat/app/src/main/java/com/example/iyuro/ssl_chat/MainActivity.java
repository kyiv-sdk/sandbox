package com.example.iyuro.ssl_chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.example.browser.url.URL_DownloadActivity;
import com.example.fingerprint.SecureLoginActivity;
import com.example.chatlibrary.chat.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    public static boolean isSSLEnabled = true;
    private final int REQUEST_APP_ENTRY_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callSecureLogin();
    }

    private void callSecureLogin(){
        Intent intent = new Intent(this, SecureLoginActivity.class);
        startActivityForResult(intent, REQUEST_APP_ENTRY_ACTIVITY);
    }

    private void initActivity(){
        setContentView(R.layout.activity_main);
        Switch switcher = findViewById(R.id.enable_ssl_switcher);
        Button startChattingBtn = findViewById(R.id.start_chatting_btn);

        startChattingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSSLEnabled = switcher.isChecked();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra("isSSLEnabled", isSSLEnabled);
                startActivity(intent);
            }
        });

        Button startBrowserBtn = findViewById(R.id.start_browser_btn);

        startBrowserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), URL_DownloadActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                initActivity();
            } else {
                Toast.makeText(getApplicationContext(), "Please, try again", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SecureLoginActivity.class);
                startActivityForResult(intent, REQUEST_APP_ENTRY_ACTIVITY);
            }
        }
    }
}
