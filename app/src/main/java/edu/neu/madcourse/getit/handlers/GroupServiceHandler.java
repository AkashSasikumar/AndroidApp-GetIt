package edu.neu.madcourse.getit.handlers;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.neu.madcourse.getit.models.Group;
import edu.neu.madcourse.getit.services.GroupService;

public class GroupServiceHandler extends Handler {
    private MainThreadHandler mainThreadHandler;
    private GroupService groupService;

    public GroupServiceHandler(MainThreadHandler mainThreadHandler, GroupService groupService) {
        this.mainThreadHandler = mainThreadHandler;
        this.groupService = groupService;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        String groupName = (String) msg.obj;
        getGroupByGroupName(groupName);
    }

    private void getGroupByGroupName(String groupName) {
        groupService.getGroupByGroupName(groupName, mainThreadHandler);
    }

}
