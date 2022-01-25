package com.rmc.randomchat.entity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Messages {

    private String message;
    private String currenttime;
    private boolean isSend;
    private int color;

    public Messages(String message, int color, boolean isSend){
        this.message = message;
        this.isSend = isSend;

        if (isSend) this.color = color + 0xff000000;
        else this.color = color + 0x7f000000;

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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}
