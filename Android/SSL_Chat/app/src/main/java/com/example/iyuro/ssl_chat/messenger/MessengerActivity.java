package com.example.iyuro.ssl_chat.messenger;

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
import com.example.iyuro.ssl_chat.R;
import java.util.ArrayList;

public class MessengerActivity extends AppCompatActivity implements UI_Interface {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;

    private ArrayList<UserMessage> messageList;
    private String dstId;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

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

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_PERMISSION_CODE);
        }

        EditText editText = findViewById(R.id.edittext_chatbox);

//        EditText editText = new android.support.v7.widget.AppCompatEditText(this) {
//
//            @Override
//            public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
//                final InputConnection ic = super.onCreateInputConnection(editorInfo);
//                EditorInfoCompat.setContentMimeTypes(editorInfo,
//                        new String [] {"text/plain", "image/png"});
//
//                final InputConnectionCompat.OnCommitContentListener callback =
//                        new InputConnectionCompat.OnCommitContentListener() {
//                            @Override
//                            public boolean onCommitContent(InputContentInfoCompat inputContentInfo,
//                                                           int flags, Bundle opts) {
//                                // read and display inputContentInfo asynchronously
//                                if (BuildCompat.isAtLeastNMR1() && (flags &
//                                        InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
//                                    try {
//                                        inputContentInfo.requestPermission();
//                                    }
//                                    catch (Exception e) {
//                                        return false; // return false if failed
//                                    }
//                                }
//
//                                // read and display inputContentInfo asynchronously.
//                                // call inputContentInfo.releasePermission() as needed.
//
//                                return true;  // return true if succeeded
//                            }
//                        };
//                return InputConnectionCompat.createWrapper(ic, editorInfo, callback);
//            }
//        };

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
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        mMessageRecycler = findViewById(R.id.reyclerview_message_list);
        messageList = ChatManager.getInstance().getUserMessagesByID(dstId);
        if (messageList == null) {
            Toast.makeText(this, "Message list in ChatManager is null", Toast.LENGTH_SHORT).show();
        } else {
            mMessageAdapter = new MessageListAdapter(messageList);
            mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
            mMessageRecycler.setAdapter(mMessageAdapter);

            if (messageList.size() > 0) {
                mMessageRecycler.smoothScrollToPosition(messageList.size());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChatManager.getInstance().setUIInterface(this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
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
            case MY_CAMERA_PERMISSION_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                    Intent cameraIntent = new
                            Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } else {
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
