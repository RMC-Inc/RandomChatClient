package com.rmc.randomchat.entity;

import java.io.Serializable;
import java.util.Arrays;

public class Room implements Serializable {
    private String name;
    private long id;
    private int time;
    private long onlieuser;

    private int icon;
    public int[] iconRGB;
    public int[] roomRGB;

    @Override
    public String toString() {
        return "Room{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", time=" + time +
                ", onlieuser=" + onlieuser +
                ", icon=" + icon +
                ", iconRGB=" + Arrays.toString(iconRGB) +
                ", roomRGB=" + Arrays.toString(roomRGB) +
                '}';
    }

    public Room(String name, long id, int time, long onlieuser, int icon, int[] iconRGB, int[] roomRGB) {
        this.name = name;
        this.id = id;
        this.time = time;
        this.onlieuser = onlieuser;
        this.icon = icon;
        this.iconRGB = iconRGB;
        this.roomRGB = roomRGB;
    }

    public Room(String name, int[] roomRGB, int icon, int[] iconRGB, int time) {
        this.name = name;
        this.time = time;
        this.icon = icon;
        this.iconRGB = iconRGB;
        this.roomRGB = roomRGB;
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

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
