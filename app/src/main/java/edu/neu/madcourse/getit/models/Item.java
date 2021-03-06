package edu.neu.madcourse.getit.models;

import android.graphics.Bitmap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class Item {


    private String itemID;
    private String name;
    private String quantity;
    private String preferredStore;
    private String preferredBrand;
    // private String mPostedOn;

    private String postedDateTime;
    private User userPosted;
    private User userGettingIt;
    private String instructions;
    // private int mImage;
    private Bitmap imageBitmap;
    private String postedInGroupID;
    private String userPostedID;


    private String userGettingID;


    public Item(String name, String quantity, String preferredStore, String preferredBrand, String postedDateTime,
                User userPosted, User userGettingIt, Bitmap itemImageBitmap, String instructions, String postedInGroupID){
        this.name = name;
        this.instructions = instructions;
        this.preferredStore = preferredStore;
        this.preferredBrand = preferredBrand;
        this.quantity = quantity;
        this.postedDateTime = postedDateTime;
        this.userPosted = userPosted;
        this.userGettingIt = userGettingIt;
        this.imageBitmap = itemImageBitmap;
        this.postedInGroupID = postedInGroupID;
        if (userPosted != null){
            this.userPostedID = userPosted.getUserId();
        }
        if (userGettingIt != null){
            this.userGettingID = userGettingIt.getUserId();
        }
    }

    public Item(){

    }


    public String getUserGettingID() {
        return userGettingID;
    }

    public void setUserGettingID(String userGettingID) {
        this.userGettingID = userGettingID;
    }

    public String getUserPostedID() {
        return userPostedID;
    }

    public void setUserPostedID(String userPostedID) {
        this.userPostedID = userPostedID;
    }

    public String getPostedInGroupID() {
        return postedInGroupID;
    }

    public void setPostedInGroupID(String postedInGroupID) {
        this.postedInGroupID = postedInGroupID;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap mImageBitmap) {
        this.imageBitmap = mImageBitmap;
    }

    public String getPreferredBrand() {
        return preferredBrand;
    }

    public void setPreferredBrand(String mPreferredBrand) {
        this.preferredBrand = mPreferredBrand;
    }
    public String getPostedDateTime() {
        return postedDateTime;
    }

    public void setPostedDateTime(String mPostedDateTime) {
        this.postedDateTime = mPostedDateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String mName) {
        this.name = mName;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String mDesc) {
        this.instructions = mDesc;
    }

    public String getPreferredStore() {
        return preferredStore;
    }

    public void setPreferredStore(String mPreferredStore) {
        this.preferredStore = mPreferredStore;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String mQuantity) {
        this.quantity = mQuantity;
    }

    public User getUserPosted() {
        return userPosted;
    }

    public void setUserPosted(User mUserPosted) {
        this.userPosted = mUserPosted;
    }

    public User getUserGettingIt() {
        return userGettingIt;
    }

    public void setUserGettingIt(User mUserGettingIt) {
        this.userGettingIt = mUserGettingIt;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }


    public static Comparator<Item> itemDateRecentComparator = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date1 = simpleDateFormat.parse(o1.getPostedDateTime());
                Date date2 = simpleDateFormat.parse(o2.getPostedDateTime());
                return date2.compareTo(date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    };

    public static Comparator<Item> itemDateOldestComparator = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date1 = simpleDateFormat.parse(o1.getPostedDateTime());
                Date date2 = simpleDateFormat.parse(o2.getPostedDateTime());
                return date1.compareTo(date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    };

    public static Comparator<Item> itemUserPostedComparator = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            return o1.getUserPosted().getFullName().compareTo(o2.getUserPosted().getFullName());
        }
    };

    public static Comparator<Item> itemUserGettingComparator = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            User o1_user = o1.getUserGettingIt();
            User o2_user = o2.getUserGettingIt();
            if (o1_user == null && o2_user == null){
                return 0;
            }
            else if (o1_user == null){
                return -1;
            }
            else if(o2_user == null){
                return 1;
            }
            else{
                return o1_user.getFullName().compareTo(o2_user.getFullName());
            }
        }
    };

    public static Comparator<Item> itemNameComparator = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    public String toString() {
        return "name: " + getName() + ", posted by: " + getUserPosted();
    }
}
