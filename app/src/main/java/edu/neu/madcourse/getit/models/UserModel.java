package edu.neu.madcourse.getit.models;

import java.util.List;

public class UserModel {

    private String user_name;
    private int score;
    private List<String> groups;

    public UserModel() {
    }

    public UserModel(String user_name, int score, List<String> groups) {
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
}
