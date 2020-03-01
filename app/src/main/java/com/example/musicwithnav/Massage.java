package com.example.musicwithnav;

public class Massage {

    private String message;
    private String messageID;
    private String senderID;

    //for firebase:
    public Massage() {
    }

    //for us:
    public Massage(String message, String messageID, String senderID) {
        this.message = message;
        this.messageID = messageID;
        this.senderID = senderID;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessageID() {
        return messageID;
    }
    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }
    public String getSenderID() {
        return senderID;
    }
    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    @Override
    public String toString() {
        return "Massage{" +
                "message='" + message + '\'' +
                ", messageID='" + messageID + '\'' +
                ", senderID='" + senderID + '\'' +
                '}';
    }
}
