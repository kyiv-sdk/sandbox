package com.good.mygoodsample.pick_to_send;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.internal_storage_utils.InternalStorageUtils;
import com.good.gd.icc.GDServiceListener;
import com.good.mygoodsample.R;
import com.good.gd.Activity;
import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.mygoodsample.ChatManager;
import com.good.mygoodsample.ChatUser;
import com.good.mygoodsample.SkeletonServerGDServiceListener;
import com.good.mygoodsample.UI_Interface;
import com.good.mygoodsample.login.LoginManager;

import java.util.ArrayList;
import java.util.Map;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;

public class PickToSendActivity extends AppCompatActivity implements UI_Interface, GDStateListener, ItemActionInterface {

    private static final String TAG = PickToSendActivity.class.getSimpleName();

    private boolean mLocked = true;
    private RecyclerView.Adapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String application;
    private String fileName;

    private boolean isAppRunningByUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_to_send);

        Log.i(TAG, "PickToSendActivity started");

        GDAndroid.getInstance().activityInit(this);

        Bundle bundle = getIntent().getExtras();
        boolean isSSLEnabled = false;
        if (bundle != null) {
            isSSLEnabled = bundle.getBoolean("isSSLEnabled");
            application = bundle.getString("application");
            fileName = bundle.getString("fileName");
            isAppRunningByUser = bundle.getBoolean("isAppRunningByUser");
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        LoginManager loginManager = new LoginManager(getApplicationContext(), null);
        if (!loginManager.signIn()){
//            GDServiceListener serv = SkeletonServerGDServiceListener.getInstance();
//            if (serv != null) {
//                ((SkeletonServerGDServiceListener) serv).bringToFront(application);
//            }
        }

        initRecyclerView();

        swipeRefreshLayout = findViewById(R.id.swipeRefreshPickUsersList);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestUsersList();
            }
        });

        requestUsersList();
    }

    private void initRecyclerView(){
        RecyclerView mRecyclerView = findViewById(R.id.pickUsersListRecyclerView);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, HORIZONTAL);
        mRecyclerView.addItemDecoration(itemDecor);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        ChatManager.getInstance().refreshNetworkInterface();
        ArrayList<ChatUser> allLoggedUsersList = ChatManager.getInstance().getChatUserArrayList();
        mAdapter = new PickUsersListAdapter(allLoggedUsersList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void requestUsersList() {
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
    protected void onDestroy() {
        Log.i(TAG, "onDestroy() IN");
        super.onDestroy();
        if (!isAppRunningByUser) {
            ChatManager.getInstance().closeConnection();
            isAppRunningByUser = true;
        }
        Log.i(TAG, "onDestroy() OUT");
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

    @Override
    public void onItemSelected(String dstID) {
        Log.i(TAG, "onItemPicked()");

        if (dstID != null && application != null && fileName != null){
            Log.i(TAG, "sending...()");
            byte[] file = InternalStorageUtils.readFile(fileName);
            Bitmap bitmap = BitmapFactory.decodeByteArray(file , 0, file.length);
            ChatManager.getInstance().sendPhoto(dstID, bitmap);

            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);

            finishAndRemoveTask();

            SkeletonServerGDServiceListener.bringToFront(application);
        }
    }
}
