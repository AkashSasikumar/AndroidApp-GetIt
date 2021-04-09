package edu.neu.madcourse.getit.models;

import java.util.List;

public class Group {
    private String group_name;
    private List<String> items;
    private List<String> users;

    public Group() {
    }

    public Group(String group_name, List<String> items, List<String> users) {
        this.group_name = group_name;
        this.items = items;
        this.users = users;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
