package edu.neu.madcourse.getit.callbacks;

import edu.neu.madcourse.getit.models.Group;

public class GroupServiceCallbacks {

    public interface CreateGroupTaskCallback {
        void onComplete(boolean isSuccess);
    }

    public interface GetGroupByGroupNameTaskCallback {
        void onComplete(Group group);
    }
}
