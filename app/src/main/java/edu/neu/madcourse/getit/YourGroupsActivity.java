package edu.neu.madcourse.getit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
    //EditText mGroupName, mGroupCode;
    Button join_group_btn;
    Button create_group_btn;
    Button view_my_items;
    List<GroupView> groups;
    UserService userService;
    GroupService groupService;
    FCMService fcmService;
    GroupsRVAdapter mGroupAdapter;
    RecyclerView groupsRV;
    ConstraintLayout mLayout;

    private FirebaseAuth fAuth;
    private String userID;
    private String userName;

    private static final String INTENT_GROUP_NAME = "GROUP_NAME";
    private static final String INTENT_GROUP_ID = "GROUP_ID";
    private static final String INTENT_GROUP_CODE = "GROUP_CODE";
    private static final String INTENT_LOGGED_USER_ID = "LOGGED_USER_ID";


    // Create group dialog
    private AlertDialog mCreateGroupDialog;
    private View mCreateGroupView;
    private EditText mCreateGroupName;
    private Button mCreateGroupDialogBtn;

    // Join group dialog
    private AlertDialog mJoinGroupDialog;
    private View mJoinGroupView;
    private EditText mJoinGroupCode;
    private Button mJoinGroupDialogBtn;

    // Logout dialog
    private AlertDialog logoutConfirmDialog;
    private View logoutConfirmView;
    private Button logoutConfirmYesButton;
    private Button logoutConfirmNoButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_groups);
        //getSupportActionBar().setTitle("Your Groups");
//        mGroupName = findViewById(R.id.group_name_field);
//        mGroupCode = findViewById(R.id.group_code_field);
        join_group_btn = findViewById(R.id.join_group_btn);
        create_group_btn = findViewById(R.id.create_group_btn);
        view_my_items = findViewById(R.id.view_my_items);
        mLayout = findViewById(R.id.your_groups_view);
        join_group_btn.setOnClickListener(this);
        create_group_btn.setOnClickListener(this);
        view_my_items.setOnClickListener(this);

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
                GroupView clickedGroup = groups.get(position);
                String groupName = clickedGroup.getGroupName();
                String groupID = clickedGroup.getGroupID();
                String groupCode = clickedGroup.getGroupCode();
                // Snackbar.make(groupsRV, "Clicked on group: "+ groupName, Snackbar.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), GroupItems.class);
                intent.putExtra(INTENT_GROUP_NAME, groupName);
                intent.putExtra(INTENT_GROUP_ID, groupID);
                intent.putExtra(INTENT_GROUP_CODE, groupCode);
                startActivity(intent);
            }
        });

        createCreateGroupDialog();
        createLogoutConfirmDialog();
        createJoinGroupDialog();

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
        userService.getUserByUserId(userID, new UserServiceCallbacks.GetUserByUserIdTaskCallback() {
            @Override
            public void onComplete(User user) {
                userName = user.getFullName();
                List<String> groupNames = user.getGroups();
                for (int i = 0; i < groupNames.size(); i++) {

                    groupService.getGroupNameByGroupID(groupNames.get(i), new GroupServiceCallbacks.GetGroupNameFromGroupIDCallback() {
                        @Override
                        public void onComplete(Group group) {
                            GroupView g = new GroupView(Long.toString(group.getGroup_code()),
                                    group.getGroup_name(), group.getGroupId());
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

//        String groupCode = mGroupCode.getText().toString().trim();
//        String groupName = mGroupName.getText().toString().trim();
//        mGroupCode.setText("");
//        mGroupName.setText("");

        if (v.getId() == R.id.join_group_btn) {
            showJoinGroupDialog();

        }
        else if(v.getId() == R.id.create_group_btn){
            showCreateGroupDialog();

        } else if(v.getId() == R.id.view_my_items){
            Intent intent = new Intent(getApplicationContext(), UserItems.class);
            intent.putExtra(INTENT_LOGGED_USER_ID, fAuth.getCurrentUser().getUid());
            startActivity(intent);
        }
    }

    private void createCreateGroupDialog() {
        // set layout
        AlertDialog.Builder createGroupBuilder = new AlertDialog.Builder(this);
        mCreateGroupView = getLayoutInflater().inflate(R.layout.create_group_dialog, null);
        createGroupBuilder.setView(mCreateGroupView);
        mCreateGroupDialog = createGroupBuilder.create();

        // get views
        mCreateGroupName = mCreateGroupView.findViewById(R.id.group_name_dialog);
        mCreateGroupDialogBtn = mCreateGroupView.findViewById(R.id.create_group_btn);

        mCreateGroupDialogBtn.setOnClickListener(v -> createGroupBtnOnClick());
    }

    private void createGroupBtnOnClick() {
        String groupName = mCreateGroupName.getText().toString().trim();
        // Validate input fields
        if(TextUtils.isEmpty(groupName)){
            mCreateGroupName.setError("Group Name is required.");
            return;
        }

        mCreateGroupDialog.hide();

        groupService.createGroup(groupName, new GroupServiceCallbacks.CreateGroupTaskCallback() {
            @Override
            public void onComplete(Group group) {
                if (group != null){
                    // add current user to the created group
                    groupService.addUserToGroupByGroupIdAndUserId(userID, group.getGroupId(), new GroupServiceCallbacks.AddUserToGroupTaskCallback() {
                        @Override
                        public void onComplete(boolean isSuccess) {
                            if (isSuccess){
                                groups.add(new GroupView( Long.toString(group.getGroup_code()),
                                        groupName, group.getGroupId()));
                                mGroupAdapter.notifyDataSetChanged();
                                Snackbar.make(mLayout, "Group " + groupName + " created successfully!", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    Snackbar.make(mCreateGroupView, "Sorry, could not create a group!", Snackbar.LENGTH_LONG).show();
                }
            }
        });


    }

    private void showCreateGroupDialog() {
        // show the input dialog
        mCreateGroupName.setText("");
        mCreateGroupDialog.show();
    }


    private void createJoinGroupDialog() {
        // set layout
        AlertDialog.Builder joinGroupBuilder = new AlertDialog.Builder(this);
        mJoinGroupView = getLayoutInflater().inflate(R.layout.join_group_dialog, null);
        joinGroupBuilder.setView(mJoinGroupView);
        mJoinGroupDialog = joinGroupBuilder.create();

        // get views
        mJoinGroupCode = mJoinGroupView.findViewById(R.id.join_group_code);
        mJoinGroupDialogBtn = mJoinGroupView.findViewById(R.id.join_group_dialog_btn);

        mJoinGroupDialogBtn.setOnClickListener(v -> joinGroupBtnOnClick());
    }

    private void joinGroupBtnOnClick() {

        String groupCode = mJoinGroupCode.getText().toString().trim();
        // Validate input fields
        if(TextUtils.isEmpty(groupCode)){
            mJoinGroupCode.setError("Group Code is required.");
            return;
        }

        mJoinGroupDialog.hide();


        for(GroupView group : groups) {
            if(group.getGroupCode().equals(groupCode)) {
                Snackbar.make(mLayout, "You are already a member of " + group.getGroupName(), Snackbar.LENGTH_LONG).show();
                return;
            }
        }

        fcmService.sendNewGroupMemberNotification(groupCode, userName, new FCMServiceCallBacks.sendNewGroupMemberNotificationCallback() {
            @Override
            public void onComplete() {
                groupService.addUserToGroupByGroupCode(userID, groupCode, new GroupServiceCallbacks.AddUserByGroupCodeTaskCallback() {
                    public void onComplete(Group group) {
                        if ( group!= null){
                            groups.add(new GroupView( Long.toString(group.getGroup_code()) ,
                                    group.getGroup_name(), group.getGroupId()));
                            mGroupAdapter.notifyDataSetChanged();
                            Snackbar.make(mLayout, "Joined group " + group.getGroup_name() + " successfully", Snackbar.LENGTH_LONG).show();

                        }else{
                            Snackbar.make(mLayout, "Sorry, group with the given code does not exist!", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


    }

    private void showJoinGroupDialog() {
        // show the input dialog
        mJoinGroupCode.setText("");
        mJoinGroupDialog.show();
    }

    private void createLogoutConfirmDialog() {
        AlertDialog.Builder logoutConfirmBuilder = new AlertDialog.Builder(this);
        logoutConfirmView = getLayoutInflater().inflate(R.layout.logout_confirm_layout, null);
        logoutConfirmBuilder.setView(logoutConfirmView);
        logoutConfirmDialog = logoutConfirmBuilder.create();

        // get views
        logoutConfirmYesButton = logoutConfirmView.findViewById(R.id.logout_confirm_yes);
        logoutConfirmNoButton = logoutConfirmView.findViewById(R.id.logout_confirm_no);

        logoutConfirmYesButton.setOnClickListener(v -> logoutConfirmButtonYesOnClick());
        logoutConfirmNoButton.setOnClickListener(v -> logoutConfirmButtonNoOnClick());
    }

    private void logoutConfirmButtonYesOnClick() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
    }

    private void logoutConfirmButtonNoOnClick() {
        logoutConfirmDialog.hide();
    }

    @Override
    public void onBackPressed() {
        logoutConfirmDialog.show();
    }
}