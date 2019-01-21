package com.example.iyuro.ssl_chat.messenger;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.iyuro.ssl_chat.R;

import java.util.ArrayList;

public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private ArrayList<UserMessage> mMessageList;

    public MessageListAdapter(ArrayList<UserMessage> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        UserMessage message = mMessageList.get(position);

        if (!message.getSender()) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UserMessage message = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        ImageView imageView;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            imageView = itemView.findViewById(R.id.imageViewSent);
        }

        void bind(UserMessage message) {
            if (message.getMessage() != null) {
                messageText.setText(message.getMessage());
                imageView.setVisibility(View.GONE);
                messageText.setVisibility(View.VISIBLE);
            }
            if (message.getImage() != null){
                imageView.setImageBitmap(message.getImage());
                imageView.setVisibility(View.VISIBLE);
                messageText.setVisibility(View.GONE);
            }
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        ImageView imageView;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            imageView = itemView.findViewById(R.id.imageViewReceived);
        }

        void bind(UserMessage message) {
            if (message.getMessage() != null) {
                messageText.setText(message.getMessage());
                imageView.setVisibility(View.GONE);
                messageText.setVisibility(View.VISIBLE);
            }
            if (message.getImage() != null){
                imageView.setImageBitmap(message.getImage());
                imageView.setVisibility(View.VISIBLE);
                messageText.setVisibility(View.GONE);
            }
        }
    }
}
