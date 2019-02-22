package com.good.gd.example.services.greetings.client.file_picker;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.good.gd.example.services.greetings.client.R;

import java.util.ArrayList;

public class LocalFilesRecyclerViewAdapter extends RecyclerView.Adapter<LocalFilesRecyclerViewAdapter.ViewHolder> {
    private RecyclerViewItemClickInterface recyclerViewItemClickInterface;
    private ArrayList<String> fileNameList;

    public LocalFilesRecyclerViewAdapter(RecyclerViewItemClickInterface recyclerViewItemClickInterface, ArrayList<String> fileNameList) {
        this.recyclerViewItemClickInterface = recyclerViewItemClickInterface;
        this.fileNameList = fileNameList;
    }

    @Override
    public LocalFilesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                              int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.local_file_item, null, false);
        itemLayoutView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        String text = fileNameList.get(position);
        viewHolder.txtViewUsername.setText(text);
        viewHolder.userListEntity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = viewHolder.getAdapterPosition();
                recyclerViewItemClickInterface.onItemClick(pos);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout userListEntity;
        public TextView txtViewUsername;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewUsername = itemLayoutView.findViewById(R.id.local_file_item_title);
            userListEntity = itemLayoutView.findViewById(R.id.local_file_item_entity);
        }
    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }
}
