package com.rmc.randomchat.entity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Messages {

    private String message;
    private String currenttime;
    private boolean isSend;

    public Messages(String message, boolean isSend){
        this.message = message;
        this.isSend = isSend;

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        Calendar calendar = new GregorianCalendar();
        dateFormat.setTimeZone(calendar.getTimeZone());
        this.currenttime = dateFormat.format(calendar.getTime());
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
