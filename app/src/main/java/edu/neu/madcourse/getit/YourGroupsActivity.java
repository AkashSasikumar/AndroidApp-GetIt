package edu.neu.madcourse.getit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.neu.madcourse.getit.callbacks.UserServiceCallbacks;
import edu.neu.madcourse.getit.models.User;
import edu.neu.madcourse.getit.services.UserService;

public class YourGroupsActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mGroupName, mGroupCode;
    Button join_group_btn;
    List<GroupView> groups;
    UserService userService;
    private FirebaseAuth fAuth;

    private static final String INTENT_GROUP_NAME = "GROUP_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_groups);
        getSupportActionBar().setTitle("Your Groups");
        mGroupName = findViewById(R.id.group_name_field);
        mGroupCode = findViewById(R.id.group_code_field);
        join_group_btn = findViewById(R.id.join_group_btn);
        join_group_btn.setOnClickListener(this);
        final RecyclerView groupsRV = findViewById(R.id.recyclerView);
        groups = new ArrayList<>();
        userService = new UserService();
        fAuth = FirebaseAuth.getInstance();

        String email = fAuth.getCurrentUser().getEmail();
        userService.getUserByUsername(email, new UserServiceCallbacks.GetUserByUserNameTaskCallback() {
            @Override
            public void onComplete(User user) {
                List<String> groupNames = user.getGroups();
                for (int i = 0; i < groupNames.size(); i++) {
                    GroupView g = new GroupView("GroupCode" + i, "GroupName" + groupNames.get(i));
                    groups.add(g);
                }
                final GroupsRVAdapter groupsRVAdapter = new GroupsRVAdapter(groups, YourGroupsActivity.this);

                groupsRV.setAdapter(groupsRVAdapter);
                groupsRV.setLayoutManager(new LinearLayoutManager(YourGroupsActivity.this));
            }
        });

        // hardcoding the groups.
        //TODO: get the groups from the firebase
//        for (int i = 0; i < 15; i++) {
//            GroupView g = new GroupView("GroupCode" + i, "GroupName" + i);
//            groups.add(g);
//        }
//        final GroupsRVAdapter groupsRVAdapter = new GroupsRVAdapter(groups, this);
//
//        groupsRV.setAdapter(groupsRVAdapter);
//        groupsRV.setLayoutManager(new LinearLayoutManager(this));

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.join_group_btn) {
            String groupCode = mGroupCode.getText().toString().trim();
            String groupName = mGroupName.getText().toString().trim();
            if (TextUtils.isEmpty(groupName) && TextUtils.isEmpty(groupCode)) {
                mGroupName.setError("At least one of Group Name, Group Code fields is required");
            } else {
                Snackbar.make(v, "Joined the " + groupName + " group successfully", Snackbar.LENGTH_LONG).show();
            }

            // add the user to the group and update the recycler view to reflect the new group

            // ToDo: remove test code
            Intent intent = new Intent(getApplicationContext(), GroupItems.class);
            intent.putExtra(INTENT_GROUP_NAME, "Test1");
            startActivity(intent);

        }
    }
}