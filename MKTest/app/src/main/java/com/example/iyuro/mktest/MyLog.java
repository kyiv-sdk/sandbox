package com.example.iyuro.mktest;

import android.widget.Toast;

public class MyLog {
    static {
//        System.loadLibrary("native-lib");
        System.loadLibrary("hello-jni");
    }

    public native void log(String logThis);

    public native String btn1(String logThis);
    public native int btn2(int[] array);
    public native int btn3(int x, int y);
}
