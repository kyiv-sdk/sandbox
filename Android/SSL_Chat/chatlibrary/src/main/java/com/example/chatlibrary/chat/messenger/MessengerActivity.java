package com.example.chatlibrary.chat.messenger;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatlibrary.R;
import com.example.chatlibrary.chat.ChatManager;
import com.example.chatlibrary.chat.UI_Interface;
import com.example.chatlibrary.chat.UserMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class MessengerActivity extends AppCompatActivity implements UI_Interface {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;

    private ArrayList<UserMessage> messageList;
    private String dstId;

    private static final int ALL_REQUESTS = 1888;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private String mFileName = null;
    private boolean mStartRecording = true;
    private boolean mStartPlaying = true;

    private AudioHandler audioHandler;
    private Button btnAudioRecording;
    private Button btnAudioListening;

    private EditText editText;

    String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        dstId = bundle.getString("dstId");
        try {
            getSupportActionBar().setTitle(dstId);
        } catch (Exception e){
            e.printStackTrace();
        }

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)){
            requestPermissions(PERMISSIONS,
                    ALL_REQUESTS);
        }

        editText = findViewById(R.id.edittext_chatbox);

        Button btnMessage = findViewById(R.id.button_chatbox_send);
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editText.getText().toString();
                if (msg.length() > 0) {
                    messageList.add(new UserMessage(msg, false));
                    mMessageAdapter.notifyDataSetChanged();
                    mMessageRecycler.smoothScrollToPosition(messageList.size() - 1);

                    ChatManager.getInstance().sendMessage(dstId, msg);
                }
            }
        });

        Button btnPhoto = findViewById(R.id.button_photo_send);
        btnPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkCameraPermission()){
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, ALL_REQUESTS);
                }
            }
        });

        mMessageRecycler = findViewById(R.id.reyclerview_message_list);
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

        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest" + "test" + ".3gp";

        ChatManager.getInstance().setCurrentAbsolutePath(getExternalCacheDir().getAbsolutePath());

        audioHandler = new AudioHandler();
        audioHandler.setFileName(mFileName);

        btnAudioRecording = findViewById(R.id.button_audio_record);
        btnAudioRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAudioPermission()) {
                    audioHandler.onRecord(mStartRecording);
                    if (mStartRecording) {
                        btnAudioRecording.setText("Stop recording");
                    } else {
                        btnAudioRecording.setText("Start recording");
                    }
                    mStartRecording = !mStartRecording;
                }
            }
        });

        btnAudioListening = findViewById(R.id.button_audio_listen);
        btnAudioListening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioHandler.onPlay(mStartPlaying);
                if (mStartPlaying) {
                    btnAudioListening.setText("Stop playing");
                } else {
                    btnAudioListening.setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        });

        Button btnSendAudio = findViewById(R.id.button_audio_send);
        btnSendAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newFilePath = getExternalCacheDir().getAbsolutePath() + "/audiorecordtest" + String.valueOf(ChatManager.getNextFileID()) + ".3gp";


                if (copyFile(mFileName,  newFilePath)) {
                    messageList.add(new UserMessage(false, newFilePath));
                    mMessageAdapter.notifyDataSetChanged();
                    mMessageRecycler.smoothScrollToPosition(messageList.size() - 1);

                    ChatManager.getInstance().sendAudio(dstId, newFilePath);
                } else {
                    Toast.makeText(getApplicationContext(), "Error copying files", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkCameraPermission(){
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    ALL_REQUESTS);
            return false;
        }
        return true;
    }

    private boolean checkAudioPermission(){
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                    ALL_REQUESTS);
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
        audioHandler.close();
    }

    @Override
    public void onUsersListRefresh() {
        mMessageAdapter.notifyDataSetChanged();
        mMessageRecycler.scrollToPosition(messageList.size() - 1);
    }

    @Override
    public void onNewMessage(String srcID, String message) {
        if (srcID.equals(this.dstId)){
            mMessageAdapter.notifyDataSetChanged();
            mMessageRecycler.scrollToPosition(messageList.size() - 1);
            ChatManager.getInstance().resetUnreadMessagesByUserID(dstId);
        } else {
            Toast.makeText(this, "From " + srcID + " : " + message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNewPhotoMessage(String srcID, Bitmap bitmap) {
        if (srcID.equals(this.dstId)){
            mMessageAdapter.notifyDataSetChanged();
            mMessageRecycler.scrollToPosition(messageList.size() - 1);
            ChatManager.getInstance().resetUnreadMessagesByUserID(dstId);
        } else {
            Toast.makeText(this, "From " + srcID + " : " + "photo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNewAudioMessage(String srcID, String filePath) {
        if (srcID.equals(this.dstId)){
            mMessageAdapter.notifyDataSetChanged();
            mMessageRecycler.scrollToPosition(messageList.size() - 1);
            ChatManager.getInstance().resetUnreadMessagesByUserID(dstId);
        } else {
            Toast.makeText(this, "From " + srcID + " : " + "photo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionClosed() {
        Toast.makeText(this, "Connection was closed. Restart your app.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ALL_REQUESTS && resultCode == Activity.RESULT_OK) {
            try {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Toast.makeText(this, "photo success", Toast.LENGTH_SHORT).show();

                messageList.add(new UserMessage(photo, false));
                mMessageAdapter.notifyDataSetChanged();
                mMessageRecycler.smoothScrollToPosition(messageList.size() - 1);
                ChatManager.getInstance().sendPhoto(dstId, photo);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ALL_REQUESTS:
                if (grantResults.length > 0) {
                    int i = 0;
                    for (String permission : permissions) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "permission " + permission + " granted", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "permission " + permission + " denied", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                break;
        }
    }

    public boolean copyFile(String fromFileName, String toFileName){
        final File file = new File(fromFileName);
        if (file.length() <= 0){
            return false;
        }

        FileInputStream fis = null;
        byte[] bytesArray = new byte[(int) file.length()];
        try {
            fis = new FileInputStream(file);
            fis.read(bytesArray);
            fis.close();

            File newfile = new File(toFileName);
            FileOutputStream fos = new FileOutputStream(newfile);
            fos.write(bytesArray);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
