package edu.neu.madcourse.getit.callbacks;

import edu.neu.madcourse.getit.models.User;

public class UserServiceCallbacks {

    public interface CreateUserTaskCallback {
        void onComplete(boolean isSuccess);
    }

    public interface GetUserByUserNameTaskCallback {
        void onComplete(User user);
    }

    public interface GetUserFromEmailCallback {
        void onComplete(User user);
    }
}
