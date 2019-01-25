package com.example.iyuro.socketstest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.iyuro.socketstest.url.URL_DownloadActivity;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonUrlDownload = findViewById(R.id.url_download);
        buttonUrlDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), URL_DownloadActivity.class);
                startActivity(intent);
            }
        });
    }
}