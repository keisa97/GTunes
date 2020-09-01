package com.example.musicwithnav.models;

//Firebase database rules for custom objects:
public class User {

    //properties
    private String name;
    private String uid;
    private String userProfileImageUrl;
    private String onlineStatus;
    private String userDetails;


    //empty constructor
    public User() {
    }

    public User(String name, String uid, String userProfileImageUrl, String onlineStatus) {
        this.name = name;
        this.uid = uid;
        this.userProfileImageUrl = userProfileImageUrl;
        this.onlineStatus = onlineStatus;
    }

    public User(String name, String uid, String userProfileImageUrl, String onlineStatus, String userDetails) {
        this.name = name;
        this.uid = uid;
        this.userProfileImageUrl = userProfileImageUrl;
        this.onlineStatus = onlineStatus;
        this.userDetails = userDetails;
    }

    public User(String userDetails) {
        this.userDetails = userDetails;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(String userDetails) {
        this.userDetails = userDetails;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", uid='" + uid + '\'' +
                ", userProfileImageUrl='" + userProfileImageUrl + '\'' +
                ", onlineStatus='" + onlineStatus + '\'' +
                ", userDetails='" + userDetails + '\'' +
                '}';
    }
}
