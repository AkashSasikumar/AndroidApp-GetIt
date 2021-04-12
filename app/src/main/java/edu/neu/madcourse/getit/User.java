package edu.neu.madcourse.getit;

import java.util.ArrayList;

public class User {
    private String mFirstName;
    private String mLastName;
    private String mEmail;

    // check if needed
    private ArrayList<Item> mItemsToGet;
    private ArrayList<Item> mItemsPosted;

    public User(String firstName, String lastName, String email ){
        mFirstName = firstName;
        mLastName = lastName;
        mEmail = email;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

}
