package com.example.iyuro.ssl_chat.user_list;

import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.iyuro.ssl_chat.ChatManager;
import com.example.iyuro.ssl_chat.ChatUser;
import com.example.iyuro.ssl_chat.R;
import com.example.iyuro.ssl_chat.UI_Interface;
import com.good.gd.GDAndroid;
import com.good.gd.GDAppEventListener;
import com.good.gd.GDStateListener;

import java.util.ArrayList;
import java.util.Map;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;

public class UsersListActivity extends AppCompatActivity implements UI_Interface, GDStateListener {

    private static final String TAG = UsersListActivity.class.getSimpleName();

    private boolean mLocked = true;

    private RecyclerView.Adapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        GDAndroid.getInstance().activityInit(this);

        Bundle bundle = getIntent().getExtras();
        boolean isSSLEnabled = false;
        if (bundle != null){
            isSSLEnabled = bundle.getBoolean("isSSLEnabled");
        }

        try {
            String title = "";
            if (isSSLEnabled) {
                title += "SECURE";
            } else {
                title += "NOT SECURE";
            }
            title += " | " + ChatManager.getInstance().getCurrentUserID();
            getSupportActionBar().setTitle(title);
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
    public void onNewChatMessage(String srcID, String message) {
        mAdapter.notifyDataSetChanged();

        if (!this.mLocked) {
            Toast.makeText(this, "From " + srcID + " : " + message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionClosed() {
        Toast.makeText(this, "Connection was closed. Restart your app.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNewPhotoMessage(String srcID, Bitmap bitmap) {
        mAdapter.notifyDataSetChanged();
        if (!this.mLocked) {
            Toast.makeText(this, "From " + srcID + " : " + "photo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNewAudioMessage(String srcID, String filePath) {
        mAdapter.notifyDataSetChanged();
        if (!this.mLocked) {
            Toast.makeText(this, "From " + srcID + " : " + "audio", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAuthorized() {
        //If Activity specific GDStateListener is set then its onAuthorized( ) method is called when
        //the activity is started if the App is already authorized
        Log.i(TAG, "onAuthorized()");

        this.mLocked = false;
    }

    @Override
    public void onLocked() {
        Log.i(TAG, "onLocked()");

        this.mLocked = true;
    }

    @Override
    public void onWiped() {
        Log.i(TAG, "onWiped()");
    }

    @Override
    public void onUpdateConfig(Map<String, Object> settings) {
        Log.i(TAG, "onUpdateConfig()");
    }

    @Override
    public void onUpdatePolicy(Map<String, Object> policyValues) {
        Log.i(TAG, "onUpdatePolicy()");
    }

    @Override
    public void onUpdateServices() {
        Log.i(TAG, "onUpdateServices()");
    }

    @Override
    public void onUpdateEntitlements() {
        Log.i(TAG, "onUpdateEntitlements()");
    }
}
