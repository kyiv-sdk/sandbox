package com.example.iyuro.ssl_chat.messenger;

import android.support.annotation.Nullable;
import android.support.v13.view.inputmethod.EditorInfoCompat;
import android.support.v13.view.inputmethod.InputConnectionCompat;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import android.support.v4.os.BuildCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_test);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        dstId = bundle.getString("dstId");
        try {
            getSupportActionBar().setTitle(dstId);
        } catch (Exception e){
            e.printStackTrace();
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

        Button btn = findViewById(R.id.button_chatbox_send);
        btn.setOnClickListener(new View.OnClickListener() {
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
}
