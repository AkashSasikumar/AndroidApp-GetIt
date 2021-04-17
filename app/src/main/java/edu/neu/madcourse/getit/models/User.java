package edu.neu.madcourse.getit.models;

import java.util.List;

public class User {

    private String user_name;
    private int score;
    private List<String> groups;

    public User() {
    }

    public User(String user_name, int score, List<String> groups) {
        this.user_name = user_name;
        this.score = score;
        this.groups = groups;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "User: " + getUser_name() + "\nscore: " + getScore() + "\ngroups: " + getGroups().toString();
    }
}
