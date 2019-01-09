package com.example.iyuro.mktest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("hello-jni");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MyLog myLog = new MyLog();

        Button btn1 = findViewById(R.id.button1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestData testData = getNewTestData(1);
                Toast.makeText(getApplicationContext(), "TestData = " + testData.toString(), Toast.LENGTH_SHORT).show();
                myLog.log("btn1");
            }
        });

        Button btn2 = findViewById(R.id.button2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] intArray = new int[]{ 1,2,3,4,5,6,7,8,9,10 };

                Toast.makeText(getApplicationContext(), "Sum = " + String.valueOf(myLog.btn2(intArray)), Toast.LENGTH_SHORT).show();
                myLog.log("btn2");

            }
        });

        Button btn3 = findViewById(R.id.button3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "1 + 2 = " + String.valueOf(myLog.btn3(1, 2)), Toast.LENGTH_SHORT).show();
                myLog.log("btn3");
            }
        });

        Button btn4 = findViewById(R.id.button4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLog.log("btn4");
//                callJavaMethod("it works!");
                testNetwork();
            }
        });

        Button btn5 = findViewById(R.id.button5);
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLog.log("btn5");

                ThreadManager threadManager = new ThreadManager();
                threadManager.createThreads(5);
            }
        });
    }

    public native void callJavaMethod(String str);
    public static native TestData getNewTestData(int x);
    public native void testNetwork();

    public void showToast(String s){
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

}
