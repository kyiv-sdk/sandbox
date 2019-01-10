package com.example.iyuro.socketstest.Messenger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.widget.Button;
import android.widget.EditText;

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

public class MessengerActivity extends AppCompatActivity implements MessageListener {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;

    private ArrayList<UserMessage> messageList;

    private EditText editText;
    private Button btn;
    private String dstId;

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

        // TODO: twice open connection
//        MessageHandler.getInstance().openConnection();
        MessageHandler.getInstance().setMessageListener(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editText.getText().toString();
                messageList.add(new UserMessage(msg, 1));
                mMessageAdapter.notifyDataSetChanged();

                JSONObject jsonObject = new JSONObject();
                try {
                    if (dstId != null) {
                        jsonObject.put("dstID", dstId);
                        jsonObject.put("message", msg);
                    }

                    MessageHandler.getInstance().send(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
            messageList.add(new UserMessage(data, 2));
            mMessageAdapter.notifyDataSetChanged();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageHandler.getInstance().send("exit");
        MessageHandler.getInstance().closeConnection();
    }
}
