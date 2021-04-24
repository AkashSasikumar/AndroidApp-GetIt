package edu.neu.madcourse.getit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

    // Add user dialog
    private AlertDialog mAddMemberDialog;
    private View mAddMemberView;
    private EditText mAddMemberEmail;
    private Button mAddMemberBtn;

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
        createAddMemberDialog();
    }

    private void createAddMemberDialog() {
        // set layout
        AlertDialog.Builder addUserBuilder = new AlertDialog.Builder(this);
        mAddMemberView = getLayoutInflater().inflate(R.layout.add_member_dialog, null);
        addUserBuilder.setView(mAddMemberView);
        mAddMemberDialog = addUserBuilder.create();

        // get views
        mAddMemberEmail = mAddMemberView.findViewById(R.id.add_member_email);
        mAddMemberBtn = mAddMemberView.findViewById(R.id.add_member_btn);

        mAddMemberBtn.setOnClickListener(v -> addMemberToGroup());
    }

    private void showAddMemberDialog() {
        // show the input dialog
        mAddMemberEmail.setText("");
        mAddMemberDialog.show();
    }

    private void addMemberToGroup() {
        String mEmail = mAddMemberEmail.getText().toString().trim();
        // Validate input fields
        if(TextUtils.isEmpty(mEmail)){
            mAddMemberEmail.setError("Email Id is required");
            return;
        }


        groupService.addUserToGroupByGroupIDAndEmail(mEmail, groupID, new GroupServiceCallbacks.AddUserToGroupByGroupIDAndEmailCallback() {
            @Override
            public void onComplete(User user) {
                if (user != null){
                    mAddMemberDialog.hide();
                    mUsers.add(new UserCard(user.getFullName(), user.getUserEmail(), user.getScore()));
                    mUserAdaptor.notifyDataSetChanged();
                    Snackbar.make(mRecyclerView, mEmail + " added to group!", Snackbar.LENGTH_LONG).show();
                }else{
                    mAddMemberDialog.hide();
                    Snackbar.make(mRecyclerView, mEmail + " does not exist!", Snackbar.LENGTH_LONG).show();
                }

            }
        });

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
        getSupportActionBar().setTitle("Users (" +title+ ")");
    }

    private void populateUsersInTheGroup(){

        groupService.getGroupByGroupCode(groupCode, new GroupServiceCallbacks.GetGroupByGroupCodeCallback() {
            @Override
            public void onComplete(Group group) {
                List<String> users =  group.getUsers();
                for (String user : users){
                    userService.getUserByUserId(user, new UserServiceCallbacks.GetUserByUserIdTaskCallback() {
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
            showAddMemberDialog();
        }
    }

}