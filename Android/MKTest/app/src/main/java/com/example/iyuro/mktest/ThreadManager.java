package com.example.iyuro.mktest;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ThreadManager {
    public ThreadManager() {
    }

    public void createThreads(int tNum){
        for (int i = 0; i < tNum; i++){
            TestData testData = newThread(i);
            Log.i("MY_LOG", testData.toString());
        }
    }

    public native TestData newThread(int id);
}