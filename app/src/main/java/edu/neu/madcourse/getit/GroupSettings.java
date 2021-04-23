package edu.neu.madcourse.getit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class GroupSettings extends AppCompatActivity {

    private List<UserCard> mUsers = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private UserAdaptor mUserAdaptor;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        populateUsersInTheGroup();
        setupRecyclerView();
    }

    private void setupRecyclerView(){
        mRecyclerView = findViewById(R.id.recycler_users);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mUserAdaptor = new UserAdaptor(mUsers);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mUserAdaptor);
        mUserAdaptor.setOnUserClickListener(new UserAdaptor.OnUserClickListener() {
            @Override
            public void onUserClick(int position) {
                Snackbar.make(mRecyclerView, "Hello "+ mUsers.get(position).getUserName() +"!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void populateUsersInTheGroup(){
        for (int i = 1; i <= 15; i++){
            UserCard userCard = new UserCard("Test User", "test@gmail.com", 999);
            mUsers.add(userCard);
        }
    }
}