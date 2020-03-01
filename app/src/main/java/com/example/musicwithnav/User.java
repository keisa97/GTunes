package com.example.musicwithnav;

//Firebase database rules for custom objects:
public class User {

    //properties
    private String name;
    private String uid;
    private String userProfileImageUrl;

    //empty constructor


    public User(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }

    public User() {
    }

    public String getUid() {
        return uid;
    }


    public String getName() {
        return name;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
