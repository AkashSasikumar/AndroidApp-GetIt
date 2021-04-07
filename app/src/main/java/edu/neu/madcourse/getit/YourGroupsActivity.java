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

import java.util.List;

public class YourGroupsActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mGroupName, mGroupCode;
    Button join_group_btn;
    List<GroupView> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_groups);
        mGroupName = findViewById(R.id.group_name_field);
        mGroupCode = findViewById(R.id.group_code_field);
        join_group_btn = findViewById(R.id.join_group_btn);
        join_group_btn.setOnClickListener(this);
        final RecyclerView groupsRV = findViewById(R.id.recyclerView);
        final GroupsRVAdapter groupsRVAdapter = new GroupsRVAdapter(groups, this);

//        groupsRV.setAdapter(groupsRVAdapter);
//        groupsRV.setLayoutManager(new LinearLayoutManager(this));


    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.join_group_btn) {
            //startActivity(new Intent(getApplicationContext(), Login.class));
            String groupCode = mGroupCode.getText().toString().trim();
            String groupName = mGroupCode.getText().toString().trim();
            if (TextUtils.isEmpty(groupName) && TextUtils.isEmpty(groupCode)) {
                mGroupName.setError("At least one of Group Name, Group Code fields is required");
                return;
            }

            // add the user to the group and update the recycler view to reflect the new group

        }
    }
}