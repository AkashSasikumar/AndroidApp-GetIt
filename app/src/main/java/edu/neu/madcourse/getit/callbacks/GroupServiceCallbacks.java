package edu.neu.madcourse.getit.callbacks;

import edu.neu.madcourse.getit.models.Group;

public class GroupServiceCallbacks {

    public interface CreateGroupTaskCallback {
        void onComplete(Group group);
    }

    public interface GetGroupByGroupNameTaskCallback {
        void onComplete(Group group);
    }

    public interface AddUserToGroupTaskCallback {
        void onComplete(boolean isSuccess);
    }

    public interface AddItemToGroupTaskCallback {
        void onComplete(boolean isSuccess);
    }

    public interface AddUserByGroupCodeTaskCallback {
        void onComplete(Group group);
    }

    public interface GetGroupNameFromGroupIDCallback{
        void onComplete(Group group);
    }

    public interface  GetGroupByGroupCodeCallback {
        void onComplete(Group group);
    }
}
