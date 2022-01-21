package com.rmc.randomchat.entity;

import java.io.Serializable;

public class Room implements Serializable {
    private String name;
    private long id;
    private int time;
    private long onlieuser;
    private int roomColor;

    public Room() {

    }

    @Override
    public String toString() {
        return "Room{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", time=" + time +
                ", onlieuser=" + onlieuser +
                ", roomColor=" + roomColor +
                '}';
    }

    public Room(String name, long id, int time, long onlieuser,int roomColor) {
        this.name = name;
        this.id = id;
        this.time = time;
        this.onlieuser = onlieuser;
        this.roomColor = roomColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public long getOnlieuser() {
        return onlieuser;
    }

    public void setOnlieuser(long onlieuser) {
        this.onlieuser = onlieuser;
    }

    public int getRoomColor() {
        return roomColor;
    }

    public void setRoomColor(int roomColor) {
        this.roomColor = roomColor;
    }
}
