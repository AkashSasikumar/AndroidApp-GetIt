package edu.neu.madcourse.getit.handlers;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.annotation.NonNull;

import edu.neu.madcourse.getit.models.Group;

public class MainThreadHandler extends Handler {

    private TextView groupNameTextView;

    public MainThreadHandler(TextView groupNameTextView) {
        this.groupNameTextView = groupNameTextView;
    }

    @Override
    public void handleMessage(@NonNull Message group_msg) {
        updateUIProcessor(group_msg);
    }

    private void updateUIProcessor(Message msg) {
        Group group = (Group) msg.obj;
        groupNameTextView.setText(group.getGroup_name());
    }



}
