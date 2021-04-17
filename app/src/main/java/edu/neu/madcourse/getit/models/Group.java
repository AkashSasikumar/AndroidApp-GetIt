package edu.neu.madcourse.getit.models;

import java.util.List;

public class Group {
    private String groupId;
    private String group_name;
    private List<String> items_to_purchase;
    private List<String> items_purchased;
    private List<String> users;

    public Group() {
    }

    public Group(String groupId, String group_name, List<String> items_to_purchase, List<String> items_purchased, List<String> users) {
        this.groupId = groupId;
        this.group_name = group_name;
        this.items_to_purchase = items_to_purchase;
        this.items_purchased = items_purchased;
        this.users = users;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public List<String> getItems_to_purchase() {
        return items_to_purchase;
    }

    public void setItems_to_purchase(List<String> items_to_purchase) {
        this.items_to_purchase = items_to_purchase;
    }

    public List<String> getItems_purchased() {
        return items_purchased;
    }

    public void setItems_purchased(List<String> items_purchased) {
        this.items_purchased = items_purchased;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "group id: " + getGroupId() + "\nname: " + getGroup_name() + "\nusers: " + getUsers().toString();
    }
}
