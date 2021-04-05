package edu.neu.madcourse.getit;

public class GroupView {
    private String groupCode;
    private String groupName;

    public GroupView(String groupCode, String groupName) {
        this.groupCode = groupCode;
        this.groupName = groupName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public String getGroupName() {
        return groupName;
    }
}
