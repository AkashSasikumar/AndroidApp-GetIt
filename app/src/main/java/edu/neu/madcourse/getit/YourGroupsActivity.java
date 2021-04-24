package edu.neu.madcourse.getit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.getit.callbacks.FCMServiceCallBacks;
import edu.neu.madcourse.getit.callbacks.GroupServiceCallbacks;
import edu.neu.madcourse.getit.callbacks.UserServiceCallbacks;
import edu.neu.madcourse.getit.models.Group;
import edu.neu.madcourse.getit.models.User;
import edu.neu.madcourse.getit.services.FCMService;
import edu.neu.madcourse.getit.services.GroupService;
import edu.neu.madcourse.getit.services.UserService;

public class YourGroupsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String FETCHING_FCM_REGISTRATION_TOKEN_STATUS = "FETCHING_FCM_REGISTRATION_TOKEN_STATUS:  ";
    EditText mGroupName, mGroupCode;
    Button join_group_btn;
    Button create_group_btn;
    List<GroupView> groups;
    UserService userService;
    GroupService groupService;
    FCMService fcmService;
    GroupsRVAdapter mGroupAdapter;
    RecyclerView groupsRV;
    private FirebaseAuth fAuth;
    private String userID;
    private String userName;

    private static final String INTENT_GROUP_NAME = "GROUP_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_groups);
        getSupportActionBar().setTitle("Your Groups");
        mGroupName = findViewById(R.id.group_name_field);
        mGroupCode = findViewById(R.id.group_code_field);
        join_group_btn = findViewById(R.id.join_group_btn);
        create_group_btn = findViewById(R.id.create_group_btn);
        join_group_btn.setOnClickListener(this);
        create_group_btn.setOnClickListener(this);

        // recycler view
        groupsRV = findViewById(R.id.recyclerView);
        groupsRV.setHasFixedSize(true);
        groups = new ArrayList<>();
        mGroupAdapter = new GroupsRVAdapter(groups);
        groupsRV.setAdapter(mGroupAdapter);
        groupsRV.setLayoutManager(new LinearLayoutManager(YourGroupsActivity.this));
        mGroupAdapter.setOnGroupClickListener(new GroupsRVAdapter.OnGroupClickListener() {
            @Override
            public void onGroupClick(int position) {
                String groupName = groups.get(position).getGroupName();
                Snackbar.make(groupsRV, "Clicked on group: "+ groupName, Snackbar.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), GroupItems.class);
                intent.putExtra(INTENT_GROUP_NAME, groupName);
                startActivity(intent);
            }
        });

        userService = new UserService();
        groupService = new GroupService();
        fcmService = new FCMService();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        fAuth = FirebaseAuth.getInstance();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d(FETCHING_FCM_REGISTRATION_TOKEN_STATUS, "failed ", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d(FETCHING_FCM_REGISTRATION_TOKEN_STATUS, "success for user:  " + fAuth.getCurrentUser().getUid() + " token: " + token);

                        userService.updateUserDeviceToken(fAuth.getCurrentUser().getUid() , token);
                    }
                });

        // get the user info from firebase
        userService.getUserByUserId(userID, new UserServiceCallbacks.GetUserByUserNameTaskCallback() {
            @Override
            public void onComplete(User user) {
                userName = user.getFullName();
                List<String> groupNames = user.getGroups();
                for (int i = 0; i < groupNames.size(); i++) {

                    groupService.getGroupNameByGroupID(groupNames.get(i), new GroupServiceCallbacks.GetGroupNameFromGroupIDCallback() {
                        @Override
                        public void onComplete(Group group) {
                            GroupView g = new GroupView(Long.toString(group.getGroup_code()), group.getGroup_name());
                            groups.add(g);
                            mGroupAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });



    }


    @Override
    public void onClick(View v) {

        String groupCode = mGroupCode.getText().toString().trim();
        String groupName = mGroupName.getText().toString().trim();

        if (v.getId() == R.id.join_group_btn) {

            if (TextUtils.isEmpty(groupCode)) {
                mGroupCode.setError("Group Code is required to join a group!");
                return;
            }




            fcmService.sendNewGroupMemberNotification(groupCode, userName, new FCMServiceCallBacks.sendNewGroupMemberNotificationCallback() {
                @Override
                public void onComplete() {
                    groupService.addUserToGroupByGroupCode(userID, groupCode, new GroupServiceCallbacks.AddUserByGroupCodeTaskCallback() {
                        @Override
                        public void onComplete(Group group) {
                            if ( group!= null){
                                groups.add(new GroupView( Long.toString(group.getGroup_code()) , group.getGroup_name()));
                                mGroupAdapter.notifyDataSetChanged();
                                Snackbar.make(v, "Joined group " + group.getGroup_name() + " successfully", Snackbar.LENGTH_LONG).show();

                            }else{
                                Snackbar.make(v, "Sorry, group with the given code does not exist!", Snackbar.LENGTH_LONG).show();
                            }

                        }
                    });
                }
            });


            // add the user to the group and update the recycler view to reflect the new group

            // ToDo: remove test code
//            Intent intent = new Intent(getApplicationContext(), GroupItems.class);
//            intent.putExtra(INTENT_GROUP_NAME, "test-group-1");
//            startActivity(intent);

        }
        else if(v.getId() == R.id.create_group_btn){
            if (TextUtils.isEmpty(groupName)) {
                mGroupName.setError("Group Name is required to create a Group!");
                return;
            }
            groupService.createGroup(groupName, new GroupServiceCallbacks.CreateGroupTaskCallback() {
                @Override
                public void onComplete(Group group) {
                    if (group != null){
                        // add current user to the created group
                        groupService.addUserToGroup(userID, groupName, new GroupServiceCallbacks.AddUserToGroupTaskCallback() {
                            @Override
                            public void onComplete(boolean isSuccess) {
                                if (isSuccess){
                                    groups.add(new GroupView( Long.toString(group.getGroup_code()), groupName));
                                    mGroupAdapter.notifyDataSetChanged();
                                    Snackbar.make(v, "Group " + groupName + " created successfully!", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                    }else{
                        Snackbar.make(v, "Sorry, could not create a group!", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}