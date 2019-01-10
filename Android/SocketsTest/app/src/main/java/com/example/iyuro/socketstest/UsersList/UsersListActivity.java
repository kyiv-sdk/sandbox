package com.example.iyuro.socketstest.UsersList;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.iyuro.socketstest.R;

import java.util.ArrayList;

public class UsersListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        mRecyclerView = findViewById(R.id.usersListRecyclerView);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<String> allUsers = new ArrayList<>();
        allUsers.add("User 1");
        allUsers.add("User 2");
        mAdapter = new MyAdapter(allUsers, this);
        mRecyclerView.setAdapter(mAdapter);
    }
}
