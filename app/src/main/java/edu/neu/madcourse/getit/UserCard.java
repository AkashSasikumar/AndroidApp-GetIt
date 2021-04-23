package edu.neu.madcourse.getit;

public class UserCard {

    private String mUserName;
    private String mUserEmail;
    private long mUserScore;

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getUserEmail() {
        return mUserEmail;
    }

    public void setUserEmail(String mUserEmail) {
        this.mUserEmail = mUserEmail;
    }

    public long getUserScore() {
        return mUserScore;
    }

    public void setUserScore(long mUserScore) {
        this.mUserScore = mUserScore;
    }

    public UserCard(String userName, String userEmail, long userScore) {
        this.mUserName = userName;
        this.mUserEmail = userEmail;
        this.mUserScore = userScore;
    }
}
