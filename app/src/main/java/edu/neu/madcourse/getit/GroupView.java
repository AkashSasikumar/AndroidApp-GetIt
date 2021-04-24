package edu.neu.madcourse.getit;

public class GroupView {
    private String groupCode;
    private String groupName;
    private String groupID;

    public GroupView(String groupCode, String groupName, String groupID) {
        this.groupCode = groupCode;
        this.groupName = groupName;
        this.groupID = groupID;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupID() {
        return groupID;
    }
}
