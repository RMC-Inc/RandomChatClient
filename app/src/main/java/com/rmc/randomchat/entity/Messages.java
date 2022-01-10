package com.rmc.randomchat.entity;

public class Messages {

    private String message;
    private String currenttime;
    private boolean isSend;

    public Messages(String message, boolean isSend, String currenttime) {
        this.message = message;
        this.isSend = isSend;
        this.currenttime = currenttime;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public String getCurrenttime() {
        return currenttime;
    }

    public void setCurrenttime(String currenttime) {
        this.currenttime = currenttime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
