package com.example.iyuro.ssl_chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.example.iyuro.ssl_chat.register.LoginActivity;
import com.example.iyuro.ssl_chat.user_list.UsersListActivity;

public class MainActivity extends AppCompatActivity {

    public static boolean isSSLEnabled = true;

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Switch switcher = findViewById(R.id.enable_ssl_switcher);
        Button startChattingBtn = findViewById(R.id.start_chatting_btn);

        startChattingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSSLEnabled = switcher.isChecked();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
