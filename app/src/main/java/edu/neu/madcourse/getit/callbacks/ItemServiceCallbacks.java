package edu.neu.madcourse.getit.callbacks;

import edu.neu.madcourse.getit.models.Item;

public class ItemServiceCallbacks {

    public interface CreateItemTaskCallback {
        void onComplete(boolean isSuccess);
    }

    public interface GetItemByItemIdTaskCallback {
        void onComplete(Item item);
    }
}