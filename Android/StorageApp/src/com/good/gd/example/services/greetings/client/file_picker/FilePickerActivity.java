package com.good.gd.example.services.greetings.client.file_picker;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.example.services.greetings.client.R;
import com.good.gd.example.services.greetings.client.file_source.FileSourceActivity;

import java.util.ArrayList;
import java.util.Map;

public class FilePickerActivity extends AppCompatActivity implements GDStateListener, FilePickerViewInterface, RecyclerViewItemClickInterface{
    private static final String TAG = FilePickerActivity.class.getSimpleName();

    private FilePickerPresenterInterface filePickerPresenterInterface;
    private FilePickerModelInterface filePickerModelInterface;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate()");

        GDAndroid.getInstance().activityInit(this);

        setContentView(R.layout.activity_file_picker);

        this.filePickerModelInterface = new FilePickerModel();
        this.filePickerPresenterInterface = new FilePickerPresenter(this, filePickerModelInterface);

        filePickerPresenterInterface.initRecyclerView();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filePickerPresenterInterface.handleOnAddFileButtonClick();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        filePickerPresenterInterface.handleActivityOnResume();
    }

    @Override
    public void onAuthorized() {
        Log.i(TAG, "onAuthorized()");
    }

    @Override
    public void onLocked() {
        Log.i(TAG, "onLocked()");
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

    @Override
    public void refreshData() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void openFileSourceActivity() {
        Intent fileSourceIntent = new Intent(getApplicationContext(), FileSourceActivity.class);
        startActivity(fileSourceIntent);
    }

    @Override
    public void initRecyclerView(ArrayList<String> files) {
        mRecyclerView = findViewById(R.id.local_files_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter = new LocalFilesRecyclerViewAdapter(this, files);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(int position) {
        Log.i(TAG, "onItemClick(); pos = " + position);
        filePickerPresenterInterface.handleOnFileItemClick(position);
    }
}
