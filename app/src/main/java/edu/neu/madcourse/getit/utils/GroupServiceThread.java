package edu.neu.madcourse.getit.utils;

import android.os.Looper;

import edu.neu.madcourse.getit.handlers.GroupServiceHandler;
import edu.neu.madcourse.getit.handlers.MainThreadHandler;
import edu.neu.madcourse.getit.services.GroupService;

public class GroupServiceThread extends Thread {
    private final MainThreadHandler mainThreadHandler;
    private GroupServiceHandler groupServiceHandler;
    private GroupService groupService;

    public GroupServiceThread(MainThreadHandler mainThreadHandler, GroupService groupService) {
        this.mainThreadHandler = mainThreadHandler;
        this.groupService = groupService;
    }

    @Override
    public void run() {
        Looper.prepare();
        groupServiceHandler = new GroupServiceHandler(mainThreadHandler, groupService);
        Looper.loop();
    }

    public GroupServiceHandler getGroupServiceHandler() {
        return groupServiceHandler;
    }

    public void setGroupServiceHandler(GroupServiceHandler groupServiceHandler) {
        this.groupServiceHandler = groupServiceHandler;
    }
}
