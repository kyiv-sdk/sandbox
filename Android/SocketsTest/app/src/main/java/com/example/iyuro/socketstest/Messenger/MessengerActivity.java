package com.example.iyuro.socketstest.Messenger;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.iyuro.socketstest.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MessengerActivity extends AppCompatActivity implements MessageListener, MessageProtocolInterface {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;

    private static ArrayList<UserMessage> messageList;

    private EditText editText;
    private Button btn;
    private String dstId;
    MessageProtocol messageProtocol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_test);

        Bundle bundle = getIntent().getExtras();
        dstId = bundle.getString("dstId");

        editText = findViewById(R.id.edittext_chatbox);

        btn = findViewById(R.id.button_chatbox_send);

        mMessageRecycler = findViewById(R.id.reyclerview_message_list);
        messageList = new ArrayList<>();
        mMessageAdapter = new MessageListAdapter(messageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);

//        MessageHandler.getInstance().openConnection();
        MessageHandler.getInstance().setMessageListener(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editText.getText().toString();
                messageList.add(new UserMessage(msg, "1"));
                mMessageAdapter.notifyDataSetChanged();

                JSONObject jsonObject = new JSONObject();
                    try {
                        if (dstId != null) {
                            jsonObject.put("keyAction", "msg");
                            jsonObject.put("dstID", dstId);
                            jsonObject.put("message", msg);
                        }

                    MessageHandler.getInstance().send(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        messageProtocol = new MessageProtocol(this);
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

            messageProtocol.processReceivedMessage(data);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MessageHandler.getInstance().setMessageListener(this);
    }

    @Override
    public String getCurrentUsername() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String currentUsernameDefault = getResources().getString(R.string.current_username_default_key);
        return sharedPref.getString(getString(R.string.current_username_key), currentUsernameDefault);
    }

    @Override
    public String getCurrentDestionationUsername() {
        return dstId;
    }

    @Override
    public void onCurrentChatNewMessage(String message) {
        messageList.add(new UserMessage(message, "2"));
        mMessageAdapter.notifyDataSetChanged();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        mMessageRecycler.smoothScrollToPosition(messageList.size() - 1);
    }

    @Override
    public void onAnotherChatNewMessage(String srcID, String message) {
        Toast.makeText(this, "From " + srcID + " : " + message, Toast.LENGTH_SHORT).show();
    }
}
