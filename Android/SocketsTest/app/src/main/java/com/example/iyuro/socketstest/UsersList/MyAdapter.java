package com.example.iyuro.socketstest.UsersList;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.iyuro.socketstest.Messenger.ChatUser;
import com.example.iyuro.socketstest.Messenger.MessengerActivity;
import com.example.iyuro.socketstest.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<ChatUser> allUsernames;
    private Context context;

    public MyAdapter(ArrayList<ChatUser> allUsernames, Context context) {
        this.allUsernames = allUsernames;
        this.context = context;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                   int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_list_entity, null);

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        String text = allUsernames.get(position).getUserID() + " : " + allUsernames.get(position).getUnreadMessagesCounter();
        viewHolder.txtViewTitle.setText(text);

        viewHolder.txtViewTitle.setOnClickListener(new View.OnClickListener() {
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

        public TextView txtViewTitle;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = itemLayoutView.findViewById(R.id.entity_username);
        }
    }

    @Override
    public int getItemCount() {
        return allUsernames.size();
    }
}
