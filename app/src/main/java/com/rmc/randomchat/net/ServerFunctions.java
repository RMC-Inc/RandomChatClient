package com.rmc.randomchat.net;

import android.util.Log;

import com.rmc.randomchat.entity.Room;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ServerFunctions {

    public static void setNickname(String nick) throws IOException{
            String msg = "[" + nick + "]";
            Server.getInstance().write(Commands.CHANGE_NICKNAME, msg);
    }

    public static List<Room> getRooms (int from, int to, String roomSearch) throws IOException{
        String msg = from + " " +  to + " [" + roomSearch + "]";
        List<Room> list = new LinkedList<>();

            Server.getInstance().write(Commands.ROOM_LIST, msg);

            List<String> rooms;
            try {
                while ((rooms = Server.getInstance().read(2 * 1000)) != null){
                    rooms.addAll(Server.getInstance().read(2 * 1000));
                    rooms.forEach(roomString -> {
                        Log.println(Log.DEBUG, "SERVER", "RoomString: " + roomString);
                        Scanner roomScanner = new Scanner(roomString.substring(1));

                        long id = roomScanner.nextLong();
                        long usersCount = roomScanner.nextLong();
                        int  roomColor = roomScanner.nextInt();
                        int time = roomScanner.nextInt();
                        String name = stringInside(roomString, "[", "]");

                        Room r = new Room(name, id, time, usersCount, roomColor);
                        list.add(r);
                        Log.println(Log.DEBUG, "SERVER", "Room Added: " + r.toString());
                    });
                    break;
                }
            } catch (SocketTimeoutException e){
                e.printStackTrace();
            }
        return list;
    }


    private static String stringInside(String s, String left, String right){
        Log.println(Log.DEBUG, "ROOM", "Stringa da dividere: " + s);
        if (s == null || s.length() < 3 || !s.contains(left) || !s.contains(right)) return "???";
        else return s.split(Pattern.quote(left))[1].split(Pattern.quote(right))[0];
    }
}