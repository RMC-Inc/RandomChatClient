package com.rmc.randomchat.net;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.rmc.randomchat.entity.Room;
import com.rmc.randomchat.entity.User;

public class ServerCommImpl implements ServerComm {

    private Socket soc;
    private static ServerCommImpl instance = null;
//    private final String HostName = "2.237.250.35";
    private final String HostName = "192.168.1.24";

    private final int port = 8125;

    public static User user = new User("Guest");

    private Scanner in;
    private OutputStream out;

    private ServerCommImpl() {
        try {
            soc = new Socket(HostName, port);
            in = new Scanner(soc.getInputStream());
            out = soc.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static ServerCommImpl getInstance(){
        if (instance == null || instance.soc.isClosed()){
            instance = new ServerCommImpl();
            instance.setNickname(user.getNickname());
            return instance;
        } else return instance;
    }

    private static String stringInside(String s, String left, String right){
        Log.println(Log.DEBUG, "ROOM", "Stringa da dividere: " + s);
        if (s == null || s.length() < 3 || !s.contains(left) || !s.contains(right)) return "???";
        else return s.split(Pattern.quote(left))[1].split(Pattern.quote(right))[0];
    }

    public static boolean isClosed() {
         return instance == null || instance.soc.isClosed();
    }

    @Override
    public synchronized List<Room> getRooms (int numRooms, String roomSearch) {
        String msg = String.format(Locale.ENGLISH, "%c %d [%s]", Commands.ROOM_LIST, numRooms, roomSearch);
        List<Room> list = new ArrayList<>(numRooms);
        try {
            out.write(msg.getBytes(StandardCharsets.US_ASCII));
            out.flush();

            if (in.hasNext())
                numRooms = in.nextInt();

            for (int i = 0; i < numRooms; ++i){
                if (in.hasNextLine()){
                    long id = in.nextLong();
                    long usersCount = in.nextLong();
                    int[] roomRGB = Arrays.stream(in.next().split(Pattern.quote("."))).mapToInt(Integer::parseInt).toArray();
                    int icon = in.nextInt();
                    int[] iconRGB = Arrays.stream(in.next().split(Pattern.quote("."))).mapToInt(Integer::parseInt).toArray();
                    int time = in.nextInt();
                    String name = stringInside(in.nextLine(), "[", "]");


                    Room r = new Room(name, id, time, usersCount, icon, iconRGB, roomRGB);
                    list.add(r);
                    Log.println(Log.DEBUG, "ROOM", "Room Added: " + r.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public synchronized void setNickname(String nick) {
        String msg = String.format(Locale.ENGLISH, "%c [%s]", Commands.CHANGE_NICKNAME, nick);
        try {
            out.write(msg.getBytes(StandardCharsets.US_ASCII));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized User enterRoom (long idroom) {
        String msg = String.format(Locale.ENGLISH, "%c %d", Commands.ENTER_IN_ROOM, idroom);
        try {
            out.write(msg.getBytes(StandardCharsets.US_ASCII));
            out.flush();

            msg = waitMessage();
            if (msg == null || msg.charAt(0) == Commands.EXIT) return null;
            else return  new User(stringInside(msg, "[", "]"));
        } catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String waitMessage(){
        if (in.hasNextLine()) return in.nextLine();
        return null;
    }


    @Override
    public synchronized User nextUser() {
        String msg = String.format(Locale.ENGLISH, "%c", Commands.NEXT_USER);
        try {
            out.write(msg.getBytes(StandardCharsets.US_ASCII));
            out.flush();


            msg = waitMessage();
            if (msg == null || msg.charAt(0) == Commands.EXIT) return null;
            else return  new User(stringInside(msg, "[", "]"));
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public synchronized long createRoom(Room room) {
        String msg = String.format(Locale.ENGLISH, "%c %d.%d.%d %d %d.%d.%d %d", Commands.NEW_ROOM,
                room.roomRGB[0], room.roomRGB[1], room.roomRGB[2],
                room.getIcon(),
                room.iconRGB[0], room.iconRGB[1], room.iconRGB[2],
                room.getTime());
        try {
            out.write(msg.getBytes(StandardCharsets.US_ASCII));
            out.flush();

            if (in.hasNextLine()){
                room.setId(in.nextLong());
                return room.getId();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public synchronized void sendMessage(String message) {
        String msg = String.format(Locale.ENGLISH, "%c %s", Commands.SEND_MSG, message);
        try {
            out.write(msg.getBytes(StandardCharsets.US_ASCII));
            out.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void sendExit() {
        String msg = String.format(Locale.ENGLISH, "%c", Commands.EXIT);
        try {
            out.write(msg.getBytes(StandardCharsets.US_ASCII));
            out.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
