package com.good.mygoodsample.messenger;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.good.mygoodsample.R;
import com.good.mygoodsample.UserMessage;

import java.util.ArrayList;

public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private ArrayList<UserMessage> mMessageList;
    private Activity activity;

    public MessageListAdapter(Activity activity, ArrayList<UserMessage> mMessageList) {
        this.mMessageList = mMessageList;
        this.activity = activity;
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
        boolean isImageFitToScreen;
        Button button;
        private boolean mStartPlaying;

        private AudioHandler audioHandler;

        SentMessageHolder(final View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body_sent);
            imageView = itemView.findViewById(R.id.image_view_sent);
            button = itemView.findViewById(R.id.button_audio_listen_sent);

            isImageFitToScreen = false;

            mStartPlaying = true;

            audioHandler = null;

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int dimensionInPixel;

                    if(isImageFitToScreen) {
                        isImageFitToScreen = false;
                        dimensionInPixel = 150;
                    } else {
                        isImageFitToScreen = true;
                        dimensionInPixel = 300;
                    }

                    int dimensionInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimensionInPixel, activity.getResources().getDisplayMetrics());

                    imageView.getLayoutParams().height = dimensionInDp;
                    imageView.getLayoutParams().width = dimensionInDp;
                    itemView.requestLayout();
                }
            });

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    audioHandler.onPlay(mStartPlaying);
                    if (mStartPlaying) {
                        button.setText("Stop playing");
                    } else {
                        button.setText("Start playing");
                    }
                    mStartPlaying = !mStartPlaying;
                }
            });
        }

        void bind(UserMessage message) {
            if (message.getMessage() != null) {
                messageText.setText(message.getMessage());
                imageView.setVisibility(View.GONE);
                messageText.setVisibility(View.VISIBLE);
                button.setVisibility(View.GONE);
            } else if (message.getImage() != null){
                imageView.setImageBitmap(message.getImage());
                imageView.setVisibility(View.VISIBLE);
                messageText.setVisibility(View.GONE);
                button.setVisibility(View.GONE);
            } else if (message.getFilePath() != null){
                imageView.setVisibility(View.GONE);
                messageText.setVisibility(View.GONE);
                button.setVisibility(View.VISIBLE);
                audioHandler = new AudioHandler();
                audioHandler.setFileName(message.getFilePath());
            }
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        ImageView imageView;
        boolean isImageFitToScreen;

        Button button;
        private boolean mStartPlaying;

        private AudioHandler audioHandler;

        ReceivedMessageHolder(final View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body_received);
            imageView = itemView.findViewById(R.id.image_view_received);
            button = itemView.findViewById(R.id.button_sent_audio_received);

            isImageFitToScreen = false;

            mStartPlaying = true;

            audioHandler = null;

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int dimensionInPixel;

                    if(isImageFitToScreen) {
                        isImageFitToScreen = false;
                        dimensionInPixel = 150;
                    } else {
                        isImageFitToScreen = true;
                        dimensionInPixel = 300;
                    }

                    int dimensionInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimensionInPixel, activity.getResources().getDisplayMetrics());

                    imageView.getLayoutParams().height = dimensionInDp;
                    imageView.getLayoutParams().width = dimensionInDp;

                    itemView.requestLayout();
                }
            });

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    audioHandler.onPlay(mStartPlaying);
                    if (mStartPlaying) {
                        button.setText("Stop playing");
                    } else {
                        button.setText("Start playing");
                    }
                    mStartPlaying = !mStartPlaying;
                }
            });
        }

        void bind(UserMessage message) {
            if (message.getMessage() != null) {
                messageText.setText(message.getMessage());
                imageView.setVisibility(View.GONE);
                messageText.setVisibility(View.VISIBLE);
                button.setVisibility(View.GONE);
            } else if (message.getImage() != null){
                imageView.setImageBitmap(message.getImage());
                imageView.setVisibility(View.VISIBLE);
                messageText.setVisibility(View.GONE);
                button.setVisibility(View.GONE);
            } else if (message.getFilePath() != null){
                imageView.setVisibility(View.GONE);
                messageText.setVisibility(View.GONE);
                button.setVisibility(View.VISIBLE);
                audioHandler = new AudioHandler();
                audioHandler.setFileName(message.getFilePath());
            }
        }
    }


}
