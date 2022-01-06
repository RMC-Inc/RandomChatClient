package com.rmc.randomchat.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import com.rmc.randomchat.entity.Room;

public class ServerCommImpl implements ServerComm {

    private Socket soc;
    private static ServerCommImpl instance = null;
    private final String HostName = "192.168.1.1";
    private int port = 5096;

    private byte[] buff = new byte[500];
    private Scanner in;

    private ServerCommImpl() {
        try {
            soc = new Socket(HostName,port);
            in = new Scanner(soc.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ServerCommImpl getInstance(){
        if (instance == null){
            instance = new ServerCommImpl();
            return instance;
        } else return instance;
    }

    @Override
    public List<Room> getRooms (int numRooms, String roomSearch) {
        try {
            String msg = Commands.ROOM_LIST + " " + numRooms + " @" + roomSearch + "@";
            soc.getOutputStream().write(msg.getBytes(StandardCharsets.US_ASCII));
            soc.getOutputStream().flush();

            numRooms = in.nextInt();
            for (int i = 0; i < numRooms; ++i){
                msg = in.nextLine();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
            return null;
    }

    @Override
    public void setNickname(String nick) {
        try {
            soc.getOutputStream().write(nick.getBytes(StandardCharsets.US_ASCII));
            soc.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void enterRoom (int idroom) {

    }

    @Override
    public void createRoom(Room room) {

    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public void nextUser() {

    }

    @Override
    public void closeConnection() {

    }


}
