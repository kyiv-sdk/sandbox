package com.example.iyuro.socketstest.UsersList;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.webkit.WebResourceResponse;
import android.widget.Toast;

import com.example.iyuro.socketstest.Messenger.MessageHandler;
import com.example.iyuro.socketstest.Messenger.MessageListener;
import com.example.iyuro.socketstest.R;

import org.json.JSONArray;
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

public class UsersListActivity extends AppCompatActivity implements MessageListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<String> allLoggedUsersList;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        mRecyclerView = findViewById(R.id.usersListRecyclerView);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        allLoggedUsersList = new ArrayList<>();

        mAdapter = new MyAdapter(allLoggedUsersList, this);
        mRecyclerView.setAdapter(mAdapter);

        allLoggedUsersList.add("TestUser");

        onUsersListReceived();

        requestUsersList();

        swipeRefreshLayout = findViewById(R.id.swipeRefreshUsersList);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestUsersList();
            }
        });
    }

    public void requestUsersList(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("keyAction", "loggedUsersList");
            MessageHandler.getInstance().send(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MessageHandler.getInstance().setMessageListener(this);
    }

    @Override
    public void onMessageReceive(byte[] bytesData) {
        swipeRefreshLayout.setRefreshing(false);
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

            // TODO: parse answer
            JSONObject jsonObject = new JSONObject(data);
            try {
                String keyAction = jsonObject.getString("keyAction");
                if (keyAction.equals("loggedUsersList")){
                    allLoggedUsersList.clear();
                    JSONArray loggedUsersJSONArray = jsonObject.getJSONArray("loggedUsers");
                    for (int i = 0; i < loggedUsersJSONArray.length(); i++){
                        allLoggedUsersList.add(loggedUsersJSONArray.getString(i));
                    }
                    onUsersListReceived();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void onUsersListReceived(){
        mAdapter.notifyDataSetChanged();
    }

    public static void onNewMessageReceive(String message){

    }
}
