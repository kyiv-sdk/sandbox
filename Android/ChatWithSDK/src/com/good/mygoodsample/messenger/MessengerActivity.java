package com.good.mygoodsample.messenger;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.widget.GDEditText;
import com.good.mygoodsample.ChatManager;
import com.good.mygoodsample.R;
import com.good.mygoodsample.UI_Interface;
import com.good.mygoodsample.UserMessage;

import java.util.ArrayList;
import java.util.Map;

public class MessengerActivity extends AppCompatActivity implements UI_Interface, GDStateListener, MessengerViewInterface {

    private static final String TAG = MessengerActivity.class.getSimpleName();

    private boolean mLocked = true;

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;

    private ArrayList<UserMessage> messageList;
    private String dstId;

    private final int REQUEST_RECORD_AUDIO_PERMISSION = 2;
    private final int REQUEST_PLAY_AUDIO_PERMISSION = 3;
    private final int REQUEST_CAMERA_PERMISSION = 1;
    private final int REQUEST_CAMERA = 4;

    private Button btnAudioRecording;
    private Button btnAudioListening;

    private GDEditText editText;

    private MessengerPresenterInterface messengerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        init();
    }

    private void init(){
        GDAndroid.getInstance().activityInit(this);

        editText = findViewById(R.id.edittext_chatbox);
        mMessageRecycler = findViewById(R.id.reyclerview_message_list);
        btnAudioRecording = findViewById(R.id.button_audio_record);
        btnAudioListening = findViewById(R.id.button_audio_listen);
        Button btnSendAudio = findViewById(R.id.button_audio_send);
        Button btnSendPhoto = findViewById(R.id.button_photo_send);
        Button btnSendMessage = findViewById(R.id.button_chatbox_send);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        dstId = bundle.getString("dstId");
        try {
            getSupportActionBar().setTitle(dstId);
        } catch (Exception e){
            e.printStackTrace();
        }

        messageList = ChatManager.getInstance().getUserMessagesByID(dstId);
        if (messageList == null) {
            Toast.makeText(this, "Message list in ChatManager is null", Toast.LENGTH_SHORT).show();
        } else {
            mMessageAdapter = new MessageListAdapter(this, messageList);
            mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
            mMessageRecycler.setAdapter(mMessageAdapter);

            if (messageList.size() > 0) {
                mMessageRecycler.smoothScrollToPosition(messageList.size());
            }
        }

        messengerPresenter = new MessengerPresenter(this, messageList, dstId,
                getExternalCacheDir().getAbsolutePath() + "/");

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messengerPresenter.sendMessage(editText.getText().toString());
            }
        });

        btnSendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCameraPermission()){
                    messengerPresenter.makePhoto();
                }
            }
        });

        ChatManager.getInstance().setCurrentAbsolutePath(getExternalCacheDir().getAbsolutePath());

        btnAudioRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAudioPermission(REQUEST_RECORD_AUDIO_PERMISSION)) {
                    messengerPresenter.recordAudio();
                }
            }
        });

        btnAudioListening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAudioPermission(REQUEST_PLAY_AUDIO_PERMISSION)) {
                    messengerPresenter.playAudio();
                }
            }
        });

        btnSendAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messengerPresenter.sendAudio();
            }
        });
    }

    public boolean checkCameraPermission(){
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
            return false;
        }
        return true;
    }

    private boolean checkAudioPermission(int request){
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                    request);
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChatManager.getInstance().setUIInterface(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        messengerPresenter.close();
    }

    @Override
    public void onUsersListRefresh() {
        mMessageAdapter.notifyDataSetChanged();
        mMessageRecycler.scrollToPosition(messageList.size() - 1);
    }

    @Override
    public void onNewChatMessage(String srcID, String message) {
        if (srcID.equals(this.dstId)){
            mMessageAdapter.notifyDataSetChanged();
            mMessageRecycler.scrollToPosition(messageList.size() - 1);
            ChatManager.getInstance().resetUnreadMessagesByUserID(dstId);
        } else {
            if (!this.mLocked) {
                Toast.makeText(this, "From " + srcID + " : " + message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onNewPhotoMessage(String srcID, Bitmap bitmap) {
        if (srcID.equals(this.dstId)){
            mMessageAdapter.notifyDataSetChanged();
            mMessageRecycler.scrollToPosition(messageList.size() - 1);
            ChatManager.getInstance().resetUnreadMessagesByUserID(dstId);
        } else {
            if (!this.mLocked) {
                Toast.makeText(this, "From " + srcID + " : " + "photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onNewAudioMessage(String srcID, String filePath) {
        if (srcID.equals(this.dstId)){
            mMessageAdapter.notifyDataSetChanged();
            mMessageRecycler.scrollToPosition(messageList.size() - 1);
            ChatManager.getInstance().resetUnreadMessagesByUserID(dstId);
        } else {
            if (!this.mLocked) {
                Toast.makeText(this, "From " + srcID + " : " + "photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionClosed() {
        Toast.makeText(this, "Connection was closed. Restart your app.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            try {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                messengerPresenter.sendPhoto(photo);

                Toast.makeText(this, "photo success", Toast.LENGTH_SHORT).show();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "permission " + "REQUEST_CAMERA_PERMISSION" + " granted", Toast.LENGTH_LONG).show();
                        messengerPresenter.makePhoto();
                    } else {
                        Toast.makeText(this, "permission " + "REQUEST_CAMERA_PERMISSION" + " denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case REQUEST_RECORD_AUDIO_PERMISSION:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        messengerPresenter.recordAudio();
                        Toast.makeText(this, "permission " + "REQUEST_AUDIO_PERMISSION" + " granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "permission " + "REQUEST_AUDIO_PERMISSION" + " denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case REQUEST_PLAY_AUDIO_PERMISSION:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        messengerPresenter.playAudio();
                        Toast.makeText(this, "permission " + "REQUEST_AUDIO_PERMISSION" + " granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "permission " + "REQUEST_AUDIO_PERMISSION" + " denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    @Override
    public void startCameraActivity(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    @Override
    public void onMessagesListUpdate() {
        mMessageAdapter.notifyDataSetChanged();
        mMessageRecycler.smoothScrollToPosition(messageList.size() - 1);
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setBtnAudioListeningText(String text) {
        btnAudioListening.setText(text);
    }

    @Override
    public void setBtnAudioRecordingText(String text) {
        btnAudioRecording.setText(text);
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
