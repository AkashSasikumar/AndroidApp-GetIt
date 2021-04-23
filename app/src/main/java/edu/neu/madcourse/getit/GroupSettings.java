package edu.neu.madcourse.getit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.getit.callbacks.GroupServiceCallbacks;
import edu.neu.madcourse.getit.callbacks.UserServiceCallbacks;
import edu.neu.madcourse.getit.models.Group;
import edu.neu.madcourse.getit.models.User;
import edu.neu.madcourse.getit.services.GroupService;
import edu.neu.madcourse.getit.services.UserService;

public class GroupSettings extends AppCompatActivity implements View.OnClickListener {

    private List<UserCard> mUsers = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private UserAdaptor mUserAdaptor;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mAddMember;

    private static final String INTENT_GROUP_NAME = "GROUP_NAME";
    private static final String INTENT_GROUP_ID = "GROUP_ID";
    private static final String INTENT_GROUP_CODE = "GROUP_CODE";
    private String groupName;
    private String groupCode;
    private String groupID;

    private GroupService groupService;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);
        mAddMember = findViewById(R.id.add_member);
        mAddMember.setOnClickListener(this);

        // get data from intent
        groupName = getIntent().getStringExtra(INTENT_GROUP_NAME);
        groupCode = getIntent().getStringExtra(INTENT_GROUP_CODE);
        groupID = getIntent().getStringExtra(INTENT_GROUP_ID);

        groupService = new GroupService();
        userService = new UserService();

        setToolbarTitle(groupName);
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

    private void setToolbarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    private void populateUsersInTheGroup(){

        groupService.getGroupByGroupCode(groupCode, new GroupServiceCallbacks.GetGroupByGroupCodeCallback() {
            @Override
            public void onComplete(Group group) {
                List<String> users =  group.getUsers();
                for (String user : users){
                    userService.getUserByUserId(user, new UserServiceCallbacks.GetUserByUserNameTaskCallback() {
                        @Override
                        public void onComplete(User user) {
                            mUsers.add(new UserCard(user.getFullName(), user.getUserEmail(), user.getScore()));
                            mUserAdaptor.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_member){
            Snackbar.make(mRecyclerView, "Take input from user", Snackbar.LENGTH_LONG).show();
        }
    }
}