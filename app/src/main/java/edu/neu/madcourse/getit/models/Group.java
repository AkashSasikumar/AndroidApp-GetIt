package edu.neu.madcourse.getit.models;

import java.util.List;

public class Group {
    private String group_name;
    private List<String> items_to_purchase;
    private List<String> items_purchased;
    private List<String> users;

    public Group() {
    }

    public Group(String group_name, List<String> items_to_purchase, List<String> items_purchased, List<String> users) {
        this.group_name = group_name;
        this.items_to_purchase = items_to_purchase;
        this.items_purchased = items_purchased;
        this.users = users;
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
}
