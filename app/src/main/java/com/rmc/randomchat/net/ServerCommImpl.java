package com.rmc.randomchat.net;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.rmc.randomchat.entity.Room;
import com.rmc.randomchat.entity.User;

public class ServerCommImpl implements ServerComm {

    private Socket soc;
    private static ServerCommImpl instance = null;
    private final String HostName = "2.237.250.35";
//   private final String HostName = "192.168.1.24";

    private final int port = 8125;

    public static User user = new User("Guest");

    private Scanner in;
    private OutputStream out;
    private byte[] buff = new byte[500];

    private ServerCommImpl() {
        try {
            soc = new Socket(HostName, port);
            in = new Scanner(soc.getInputStream());
            out = soc.getOutputStream();
            Log.println(Log.DEBUG, "SERVER", "Connected");
        } catch (IOException e) {
            Log.println(Log.DEBUG, "SERVER", "Errore di connessione al server");
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
    public List<Room> getRooms (int numRooms, String roomSearch) {
        String msg = String.format(Locale.ENGLISH, "%c %d [%s]\n", Commands.ROOM_LIST, numRooms, roomSearch);
        Log.println(Log.DEBUG, "SERVER", "Sending: " + msg);
        List<Room> list = new ArrayList<>(numRooms);
        try {
            out.write(msg.getBytes(StandardCharsets.US_ASCII));
            out.flush();


            //msg = waitMessage();
            //if (msg == null) return list;

            //numRooms = new Scanner(msg).nextInt();
            //Log.println(Log.DEBUG, "SERVER", "N Rooms: " + numRooms);
            List<String> rooms = waitMessage(10 * 1000);
            if(rooms == null) return list;
            rooms.forEach(roomString -> {
                Log.println(Log.DEBUG, "SERVER", "RoomString: " + roomString);
                Scanner roomScanner = new Scanner(roomString);

                long id = roomScanner.nextLong();
                long usersCount = roomScanner.nextLong();
                int[] roomRGB = Arrays.stream(roomScanner.next().split(Pattern.quote("."))).mapToInt(Integer::parseInt).toArray();
                int icon = roomScanner.nextInt();
                int[] iconRGB = Arrays.stream(roomScanner.next().split(Pattern.quote("."))).mapToInt(Integer::parseInt).toArray();
                int time = roomScanner.nextInt();
                String name = stringInside(roomString, "[", "]");

                Room r = new Room(name, id, time, usersCount, icon, iconRGB, roomRGB);
                list.add(r);
                Log.println(Log.DEBUG, "SERVER", "Room Added: " + r.toString());
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public synchronized void setNickname(String nick) {
        String msg = String.format(Locale.ENGLISH, "%c [%s]\n", Commands.CHANGE_NICKNAME, nick);
        Log.println(Log.DEBUG, "SERVER", "Sending: " + msg);

        try {
            out.write(msg.getBytes(StandardCharsets.US_ASCII));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized User enterRoom (long idroom) {
        String msg = String.format(Locale.ENGLISH, "%c %d\n", Commands.ENTER_IN_ROOM, idroom);
        Log.println(Log.DEBUG, "SERVER", "Sending: " + msg);

        try {
            out.write(msg.getBytes(StandardCharsets.US_ASCII));
            out.flush();

            List<String> msgs = waitMessage();
            if (msgs == null || msgs.size() == 0 || msgs.get(0).charAt(0) == Commands.EXIT) return null;
            else return  new User(stringInside(msgs.get(0), "[", "]"));
        } catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<String> waitMessage(){
        return waitMessage(0);
    }

    @Override
    public List<String> waitMessage(int timeout){
        int prevTimeout = 0;
        try {
            prevTimeout = soc.getSoTimeout();
            soc.setSoTimeout(timeout);

            int len = soc.getInputStream().read(buff);
            Log.println(Log.DEBUG, "SERVER", "Strlen: " + len);
            if(len <= 0){
                soc.close();
                return null;
            } else {
                String msg = new String(buff, StandardCharsets.US_ASCII).substring(0, len);
                return Arrays.asList(msg.split("\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                soc.setSoTimeout(prevTimeout);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return null;
    }



    @Override
    public synchronized User nextUser() {
        String msg = String.format(Locale.ENGLISH, "%c\n", Commands.NEXT_USER);
        Log.println(Log.DEBUG, "SERVER", "Sending: " + msg);

        try {
            out.write(msg.getBytes(StandardCharsets.US_ASCII));
            out.flush();


            List<String> msgs = waitMessage();
            if (msgs == null || msgs.size() == 0 || msgs.get(0).charAt(0) == Commands.EXIT) return null;
            else return  new User(stringInside(msgs.get(0), "[", "]"));
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public synchronized long createRoom(Room room) {
        String msg = String.format(Locale.ENGLISH, "%c %d.%d.%d %d %d.%d.%d %d\n", Commands.NEW_ROOM,
                room.roomRGB[0], room.roomRGB[1], room.roomRGB[2],
                room.getIcon(),
                room.iconRGB[0], room.iconRGB[1], room.iconRGB[2],
                room.getTime());
        Log.println(Log.DEBUG, "SERVER", "Sending: " + msg);

        try {
            out.write(msg.getBytes(StandardCharsets.US_ASCII));
            out.flush();

            List<String> msgs = waitMessage();
            if (msgs == null || msgs.size() == 0){
                return -1;
            } else{
                room.setId(new Scanner(msgs.get(0)).nextLong());
                return room.getId();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public synchronized void sendMessage(String message) {
        String msg = String.format(Locale.ENGLISH, "%c %s\n", Commands.SEND_MSG, message);
        Log.println(Log.DEBUG, "SERVER", "Sending: " + msg);

        try {
            out.write(msg.getBytes(StandardCharsets.US_ASCII));
            out.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void sendExit() {
        String msg = String.format(Locale.ENGLISH, "%c\n", Commands.EXIT);
        Log.println(Log.DEBUG, "SERVER", "Sending: " + msg);

        try {
            out.write(msg.getBytes(StandardCharsets.US_ASCII));
            out.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
