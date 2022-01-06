package com.rmc.randomchat.entity;

public class Room {
    private String name;
    private String id; //int
    private String time; //int
    private String onlieuser;

    public Room(String name,String  id,String time,String onlieuser ) {
        this.name =name;
        this.id = id;
        this.time= time;
        this.onlieuser = onlieuser;
    }

    public String getOnlieuser() {
        return onlieuser;
    }

    public void setOnlieuser(String onlieuser) {
        this.onlieuser = onlieuser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
