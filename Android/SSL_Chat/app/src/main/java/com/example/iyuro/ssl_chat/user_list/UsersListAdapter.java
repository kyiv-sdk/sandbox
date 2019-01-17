package com.example.iyuro.ssl_chat.user_list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.iyuro.ssl_chat.messenger.ChatUser;
import com.example.iyuro.ssl_chat.messenger.MessengerActivity;
import com.example.iyuro.ssl_chat.R;
import com.example.iyuro.ssl_chat.R;

import java.util.ArrayList;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder> {
    private ArrayList<ChatUser> allUsernames;
    private Context context;

    public UsersListAdapter(ArrayList<ChatUser> allUsernames, Context context) {
        this.allUsernames = allUsernames;
        this.context = context;
    }

    @Override
    public UsersListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                          int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_list_entity, null, false);

        itemLayoutView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        String text = allUsernames.get(position).getUserID();
        viewHolder.txtViewUsername.setText(text);
        if (allUsernames.get(position).getUnreadMessagesCounter() <= 0) {
            viewHolder.txtViewUnreadMessages.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.txtViewUnreadMessages.setText(String.valueOf(allUsernames.get(position).getUnreadMessagesCounter()));
            viewHolder.txtViewUnreadMessages.setTextColor(Color.WHITE);
            viewHolder.txtViewUnreadMessages.setVisibility(View.VISIBLE);

        }

        viewHolder.userListEntity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = viewHolder.getAdapterPosition();
                Intent intent = new Intent(context, MessengerActivity.class);
                intent.putExtra("dstId", allUsernames.get(pos).getUserID());
                context.startActivity(intent);
            }
        });

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout userListEntity;
        public TextView txtViewUsername;
        public TextView txtViewUnreadMessages;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewUsername = itemLayoutView.findViewById(R.id.entity_username);
            txtViewUnreadMessages = itemLayoutView.findViewById(R.id.entity_username_unread_messages_counter);
            userListEntity = itemLayoutView.findViewById(R.id.userListEntity);
        }
    }

    @Override
    public int getItemCount() {
        return allUsernames.size();
    }
}
