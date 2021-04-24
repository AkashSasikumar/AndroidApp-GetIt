package edu.neu.madcourse.getit.callbacks;

public class FCMServiceCallBacks {
    public interface sendNewGroupMemberNotificationCallback {
        void onComplete();
    }

    public interface sendUserGettingItemNotificationCallback {
        void onComplete();
    }
}
