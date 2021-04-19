package edu.neu.madcourse.getit;

import android.graphics.Bitmap;
import android.media.Image;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Item {

    private String mName;
    private String mQuantity;
    private String mPreferredStore;
    private String mPreferredBrand;
    // private String mPostedOn;

    private LocalDateTime mPostedDateTime;
    private User mUserPosted;
    private User mUserGettingIt;
    private String mInstructions;
    // private int mImage;
    private Bitmap mImageBitmap;

    public Item(){

    }

    public Item(String name, String quantity, String preferredStore, String preferredBrand, LocalDateTime postedDateTime,
                User userPosted, User userGettingIt, Bitmap itemImageBitmap, String instructions){
        mName = name;
        mInstructions = instructions;
        mPreferredStore = preferredStore;
        mPreferredBrand = preferredBrand;
        mQuantity = quantity;
        mPostedDateTime = postedDateTime;
        mUserPosted = userPosted;
        mUserGettingIt = userGettingIt;
        mImageBitmap = itemImageBitmap;
    }

    public Bitmap getImageBitmap() {
        return mImageBitmap;
    }

    public void setImageBitmap(Bitmap mImageBitmap) {
        this.mImageBitmap = mImageBitmap;
    }

    public String getPreferredBrand() {
        return mPreferredBrand;
    }

    public void setPreferredBrand(String mPreferredBrand) {
        this.mPreferredBrand = mPreferredBrand;
    }
    public LocalDateTime getPostedDateTime() {
        return mPostedDateTime;
    }

    public String getPostedDateTimeAsString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm:ss");
        return dtf.format(mPostedDateTime);
    }


    public void setPostedDateTime(LocalDateTime mPostedDateTime) {
        this.mPostedDateTime = mPostedDateTime;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getInstructions() {
        return mInstructions;
    }

    public void setInstructions(String mDesc) {
        this.mInstructions = mDesc;
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
