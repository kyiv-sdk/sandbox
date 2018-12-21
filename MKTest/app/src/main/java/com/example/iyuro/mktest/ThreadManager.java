package com.example.iyuro.mktest;

import android.content.Context;
import android.widget.Toast;

public class ThreadManager {

    static {
//        System.loadLibrary("native-lib");
        System.loadLibrary("hello-jni");
    }

    Context context;

    public ThreadManager(Context c) {
        this.context = c;
        init();
    }

    public void createThreads(int tNum){
        for (int i = 0; i < tNum; i++){
//            TestData testData = MainActivity.getNewTestData(1, "NewObjCreated!");
//            Toast.makeText(context, "TestData = " + testData.toString(), Toast.LENGTH_SHORT).show();
            newThread(i);
        }
    }

    public static native void newThread(int id);
    public native void init();
}
