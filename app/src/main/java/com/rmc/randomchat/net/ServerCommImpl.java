package com.rmc.randomchat.net;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.rmc.randomchat.entity.Room;
import com.rmc.randomchat.entity.User;

public class ServerCommImpl {

    private Socket soc;
    private static ServerCommImpl instance = null;
    private final String HostName = "2.237.250.35";
    //private final String HostName = "130.25.2.130";       //todo rimuovere in versione finale. Indirizzo IP pubblico di Matteo

    private final int port = 8125;

    public static User user = new User("Guest");

    private OutputStream out;
    private byte[] buff = new byte[500];

    private ServerCommImpl() {
        try {
            soc = new Socket(HostName, port);
            out = soc.getOutputStream();
            Log.println(Log.DEBUG, "SERVER", "Connected");
        } catch (IOException e) {
            Log.println(Log.DEBUG, "SERVER", "Errore di connessione al server");
            e.printStackTrace();
        }
    }

    public static ServerCommImpl getInstance(){
        if (instance == null || instance.soc.isClosed()){
            instance = new ServerCommImpl();
            //instance.setNickname(user.getNickname());
            return instance;
        } else return instance;
    }



    public static boolean isClosed() {
         return instance == null || instance.soc.isClosed();
    }




    public User enterRoom (long idroom) {
        String msg = String.format(Locale.ENGLISH, "%c %d\n", Commands.ENTER_IN_ROOM, idroom);
        Log.println(Log.DEBUG, "SERVER", "Sending: " + msg);

        try {
            out.write(msg.getBytes(StandardCharsets.US_ASCII));
            out.flush();

            List<String> msgs = waitMessage();
            if (msgs == null || msgs.size() == 0 || msgs.get(0).charAt(0) == Commands.EXIT) return null;
            //else return  new User(stringInside(msgs.get(0), "[", "]"));
        } catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    public List<String> waitMessage() throws IOException {
        return waitMessage(0);
    }

    public List<String> waitMessage(int timeout) throws IOException {
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



    public User nextUser() {
        String msg = String.format(Locale.ENGLISH, "%c\n", Commands.NEXT_USER);
        Log.println(Log.DEBUG, "SERVER", "Sending: " + msg);

        try {
            out.write(msg.getBytes(StandardCharsets.US_ASCII));
            out.flush();


            List<String> msgs = waitMessage();
            if (msgs == null || msgs.size() == 0 || msgs.get(0).charAt(0) == Commands.EXIT) return null;
            //else return  new User(stringInside(msgs.get(0), "[", "]"));
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public long createRoom(Room room) {
        String msg = String.format(Locale.ENGLISH, "%c %d %d\n",
                Commands.NEW_ROOM,
                room.getRoomColor(),
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

    public void sendMessage(String message) {
        String msg = String.format(Locale.ENGLISH, "%c %s\n", Commands.SEND_MSG, message);
        Log.println(Log.DEBUG, "SERVER", "Sending: " + msg);

        try {
            out.write(msg.getBytes(StandardCharsets.US_ASCII));
            out.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

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
