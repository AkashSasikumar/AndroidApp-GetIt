package edu.neu.madcourse.getit.models;

import com.google.firebase.Timestamp;

public class Item {
    private String item_name;
    private String group_name;
    private String user_to_purchase;
    private String user_to_request;
    private Timestamp date_added;
    private Timestamp date_purchase;

    public Item() {
    }

    public Item(String item_name, String group_name, String user_to_purchase, String user_to_request, Timestamp date_added, Timestamp date_purchase) {
        this.item_name = item_name;
        this.group_name = group_name;
        this.user_to_purchase = user_to_purchase;
        this.user_to_request = user_to_request;
        this.date_added = date_added;
        this.date_purchase = date_purchase;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getUser_to_purchase() {
        return user_to_purchase;
    }

    public void setUser_to_purchase(String user_to_purchase) {
        this.user_to_purchase = user_to_purchase;
    }

    public String getUser_to_request() {
        return user_to_request;
    }

    public void setUser_to_request(String user_to_request) {
        this.user_to_request = user_to_request;
    }

    public Timestamp getDate_added() {
        return date_added;
    }

    public void setDate_added(Timestamp date_added) {
        this.date_added = date_added;
    }

    public Timestamp getDate_purchase() {
        return date_purchase;
    }

    public void setDate_purchase(Timestamp date_purchase) {
        this.date_purchase = date_purchase;
    }
}
