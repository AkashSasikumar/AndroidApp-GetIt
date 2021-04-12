package edu.neu.madcourse.getit;

import android.media.Image;
import android.widget.ImageButton;

public class Item {

    private String mName;
    private String mDesc;
    private String mPreferredStore;
    private String mQuantity;
    private User mUserPosted;
    private User mUserGettingIt;
    private int mImage;

    public Item(String name, String desc, String preferredStore, String quantity,
                User userPosted, User userGettingIt, int itemImage){
        mName = name;
        mDesc = desc;
        mPreferredStore = preferredStore;
        mQuantity = quantity;
        mUserPosted = userPosted;
        mUserGettingIt = userGettingIt;
        mImage = itemImage;
    }

    public int getImage() {
        return mImage;
    }

    public void setImage(int mImage) {
        this.mImage = mImage;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public String getPreferredStore() {
        return mPreferredStore;
    }

    public void setPreferredStore(String mPreferredStore) {
        this.mPreferredStore = mPreferredStore;
    }

    public String getQuantity() {
        return mQuantity;
    }

    public void setQuantity(String mQuantity) {
        this.mQuantity = mQuantity;
    }

    public User getUserPosted() {
        return mUserPosted;
    }

    public void setUserPosted(User mUserPosted) {
        this.mUserPosted = mUserPosted;
    }

    public User getUserGettingIt() {
        return mUserGettingIt;
    }

    public void setUserGettingIt(User mUserGettingIt) {
        this.mUserGettingIt = mUserGettingIt;
    }
}
