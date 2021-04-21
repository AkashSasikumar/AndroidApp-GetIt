package edu.neu.madcourse.getit.models;

import java.util.List;

public class Group {
    private String groupId;
    private String group_name;
    private List<String> items;
    private List<String> users;

    public Group() {
    }

    public Group(String groupId, String group_name, List<String> items, List<String> users) {
        this.groupId = groupId;
        this.group_name = group_name;
        this.items = items;
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

    @Override
    public String toString() {
        return "group id: " + getGroupId() + "\nname: " + getGroup_name() + "\nusers: " + getUsers().toString();
    }
}
