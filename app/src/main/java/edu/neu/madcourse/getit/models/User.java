package edu.neu.madcourse.getit.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class User {

    private String user_id;
    private String user_email, user_full_name;
    private long user_score;
    private List<String> user_groups;
    private List<String> user_items_posted;
    private List<String> user_items_getting;

    public User() {
    }

    public User(String user_email) {
        this.user_email = user_email;
        this.user_full_name = "";
        this.user_score = 0;
        this.user_groups = Collections.emptyList();
        this.user_items_posted = Collections.emptyList();
        this.user_items_getting = Collections.emptyList();
    }

    public User(String user_email, String user_full_name) {
        this.user_email = user_email;
        this.user_full_name = user_full_name;
        this.user_score = 0;
        this.user_groups = Collections.emptyList();
        this.user_items_posted = Collections.emptyList();
        this.user_items_getting = Collections.emptyList();
    }

    public User(Map<String, Object> userMap) {
        if(userMap != null) {
            this.user_email = userMap.get("userEmail").toString();
            this.user_full_name = userMap.get("fullName").toString();
        }
    }

    public User(String user_email, String user_name, int user_score, List<String> user_groups, List<String> user_items_posted, List<String> user_items_getting ) {
        this.user_email = user_email;
        this.user_full_name = user_name;
        this.user_score = user_score;
        this.user_groups = user_groups;
        this.user_items_posted =user_items_posted;
        this.user_items_getting= user_items_getting;
    }

    public String getFullName() {
        return user_full_name;
    }

    public void setFullName(String user_full_name) {
        this.user_full_name = user_full_name;
    }

    public long getScore() {
        return user_score;
    }

    public void setScore(long user_score) {
        this.user_score = user_score;
    }

    public List<String> getGroups() {
        return user_groups;
    }

    public void setGroups(List<String> user_groups) {
        this.user_groups = user_groups;
    }

    public String getUserEmail() {
        return user_email;
    }

    public void setUserEmail(String user_email) {
        this.user_email = user_email;
    }

    public List<String> getUserItemsPosted() {
        return user_items_posted;
    }

    public void setUserItemsPosted(List<String> user_items_posted) {
        this.user_items_posted = user_items_posted;
    }

    public List<String> getUserItemsGetting() {
        return user_items_getting;
    }

    public void setUserItemsGetting(List<String> user_items_getting) {
        this.user_items_getting = user_items_getting;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "user: " + getFullName() + "\nemail: " + getUserEmail() + "\nscore: " + getScore();
    }
}
