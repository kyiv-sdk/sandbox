package com.example.iyuro.socketstest.Messenger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.iyuro.socketstest.R;
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
        EditText editText = findViewById(R.id.edittext_chatbox);
        Button btn = findViewById(R.id.button_chatbox_send);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editText.getText().toString();
                messageList.add(new UserMessage(msg, false));
                mMessageAdapter.notifyDataSetChanged();
                mMessageRecycler.smoothScrollToPosition(messageList.size() - 1);

                ChatManager.getInstance().sendMessage(dstId, msg);
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
