package com.example.chatlibrary.chat.user_list;

import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.chatlibrary.chat.ChatManager;
import com.example.chatlibrary.chat.ChatUser;
import com.example.chatlibrary.chat.UI_Interface;
import com.example.chatlibrary.R;

import java.util.ArrayList;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;

public class UsersListActivity extends AppCompatActivity implements UI_Interface {

    private RecyclerView.Adapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        Bundle bundle = getIntent().getExtras();
        boolean isSSLEnabled = false;
        if (bundle != null){
            isSSLEnabled = bundle.getBoolean("isSSLEnabled");
        }

        try {
            if (isSSLEnabled) {
                getSupportActionBar().setTitle("SECURE");
            } else {
                getSupportActionBar().setTitle("NOT SECURE");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        RecyclerView mRecyclerView = findViewById(R.id.usersListRecyclerView);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, HORIZONTAL);
        mRecyclerView.addItemDecoration(itemDecor);

        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ChatManager.getInstance().refreshNetworkInterface();
        ArrayList<ChatUser> allLoggedUsersList = ChatManager.getInstance().getChatUserArrayList();

        mAdapter = new UsersListAdapter(allLoggedUsersList, this);
        mRecyclerView.setAdapter(mAdapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshUsersList);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestUsersList();
            }
        });

        requestUsersList();
    }

    public void requestUsersList(){
        byte[] request = ChatManager.getInstance().createLoggedUsersListRequest();
        ChatManager.getInstance().sendMessage(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChatManager.getInstance().setUIInterface(this);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onUsersListRefresh() {
        mAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onNewMessage(String srcID, String message) {
        mAdapter.notifyDataSetChanged();
        Toast.makeText(this, "From " + srcID + " : " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionClosed() {
        Toast.makeText(this, "Connection was closed. Restart your app.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNewPhotoMessage(String srcID, Bitmap bitmap) {
        mAdapter.notifyDataSetChanged();
        Toast.makeText(this, "From " + srcID + " : " + "photo", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNewAudioMessage(String srcID, String filePath) {
        mAdapter.notifyDataSetChanged();
        Toast.makeText(this, "From " + srcID + " : " + "audio", Toast.LENGTH_SHORT).show();
    }
}
