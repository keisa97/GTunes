package com.example.musicwithnav.models;

import com.google.firebase.database.DatabaseReference;

public class Sound {

    private String soundName;
    private String soundBeatProducerID;
    private String soundBeatProducerName;
    private String soundVocalID;
    private String soundVocalName;
    private String soundImageUrl;
    private String soundDownloadUrl;
    private String soundLength;
    private String soundID;
    private int likes;


    public Sound() {
    }

    public Sound(String soundName, String soundBeatProducerID, String soundBeatProducerName, String soundVocalID, String soundVocalName, String soundImageUrl, String soundDownloadUrl, String soundLength, String soundID, int likes) {
        this.soundName = soundName;
        this.soundBeatProducerID = soundBeatProducerID;
        this.soundBeatProducerName = soundBeatProducerName;
        this.soundVocalID = soundVocalID;
        this.soundVocalName = soundVocalName;
        this.soundImageUrl = soundImageUrl;
        this.soundDownloadUrl = soundDownloadUrl;
        this.soundLength = soundLength;
        this.soundID = soundID;
        this.likes = likes;
    }

    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }

    public String getSoundBeatProducerID() {
        return soundBeatProducerID;
    }

    public void setSoundBeatProducerID(String soundBeatProducerID) {
        this.soundBeatProducerID = soundBeatProducerID;
    }

    public String getSoundBeatProducerName() {
        return soundBeatProducerName;
    }

    public void setSoundBeatProducerName(String soundBeatProducerName) {
        this.soundBeatProducerName = soundBeatProducerName;
    }

    public String getSoundVocalID() {
        return soundVocalID;
    }

    public void setSoundVocalID(String soundVocalID) {
        this.soundVocalID = soundVocalID;
    }

    public String getSoundVocalName() {
        return soundVocalName;
    }

    public void setSoundVocalName(String soundVocalName) {
        this.soundVocalName = soundVocalName;
    }

    public String getSoundImageUrl() {
        return soundImageUrl;
    }

    public void setSoundImageUrl(String soundImageUrl) {
        this.soundImageUrl = soundImageUrl;
    }

    public String getSoundDownloadUrl() {
        return soundDownloadUrl;
    }

    public void setSoundDownloadUrl(String soundDownloadUrl) {
        this.soundDownloadUrl = soundDownloadUrl;
    }

    public String getSoundLength() {
        return soundLength;
    }

    public void setSoundLength(String soundLength) {
        this.soundLength = soundLength;
    }

    public String getSoundID() {
        return soundID;
    }

    public void setSoundID(String soundID) {
        this.soundID = soundID;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "Sound{" +
                "soundName='" + soundName + '\'' +
                ", soundBeatProducerID='" + soundBeatProducerID + '\'' +
                ", soundBeatProducerName='" + soundBeatProducerName + '\'' +
                ", soundVocalID='" + soundVocalID + '\'' +
                ", soundVocalName='" + soundVocalName + '\'' +
                ", soundImageUrl='" + soundImageUrl + '\'' +
                ", soundDownloadUrl='" + soundDownloadUrl + '\'' +
                ", soundLength='" + soundLength + '\'' +
                ", soundID='" + soundID + '\'' +
                ", likes='" + likes + '\'' +
                '}';
    }
}
