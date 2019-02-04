package com.example.iyuro.ssl_chat.timeout;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class MyTimeoutActivity extends AppCompatActivity {

    private static int TIMEOUT = 5; // seconds

    private static Handler timeoutHandler = new Handler();

    private static Runnable timeoutCallback = new Runnable() {
        @Override
        public void run() {
            startAuth();
        }
    };

    public static void resetTimer(){
        timeoutHandler.removeCallbacks(timeoutCallback);
        timeoutHandler.postDelayed(timeoutCallback, TIMEOUT);
    }

    public static void stopTimer(){
        timeoutHandler.removeCallbacks(timeoutCallback);
    }

    @Override
    public void onUserInteraction(){
        resetTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopTimer();
    }

    public static void startAuth(){

    }
}
