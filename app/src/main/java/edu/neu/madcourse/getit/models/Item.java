package edu.neu.madcourse.getit.models;

public class Item {
    private String item_name;
    private String group_name;
    private String user_name;

    public Item(String item_name, String group_name, String user_name) {
        this.item_name = item_name;
        this.group_name = group_name;
        this.user_name = user_name;
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

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
