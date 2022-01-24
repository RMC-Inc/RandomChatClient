package com.rmc.randomchat.net;

import android.util.Log;

import com.rmc.randomchat.entity.Room;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class RandomChatRepositoryImpl implements RandomChatRepository, Serializable {

    private RandomChatClient client;

    private boolean chatting = false;

    private final Consumer<ChatListener> chatThreadFun = (chatListener) -> {
        String msg = " ";
        try {
            while (chatting || msg.charAt(0) != 'e'){
                msg = client.readLine(0, null);

                switch (msg.charAt(0)){
                    case 'r':
                        chatListener.onUserFound(msg.substring(2));
                        break;
                    case 'm':
                        chatListener.onMessage(msg.substring(2));
                        break;
                    case 'n':
                        chatListener.onNextUser();
                        break;
                    case 't':
                        chatListener.onTimeExpired();
                        break;
                    case 'e':
                        //if (!chatting) return;
                        chatListener.onExit();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + msg.charAt(0));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    Thread chatThread;

    @Override
    public void connect() throws IOException{
        client = RandomChatClient.getInstance();
        client.openConnection();
    }

    @Override
    public void setNickname(String nickname) throws IOException{
        client.write(Commands.CHANGE_NICKNAME, "[" + nickname + "]");
    }

    @Override
    public List<Room> getAllRooms() throws IOException{
        return getRooms(0, 0, "");
    }

    @Override
    public List<Room> getRooms(int from, int to) throws IOException{
        return getRooms(from, to, "");
    }

    @Override
    public List<Room> getRoomsByName(String name) throws IOException{
        return getRooms(0, 0, name);
    }

    private List<Room> getRooms(int from, int to, String name) throws IOException{
        String msg = from + " " +  to + " [" +  name + "]";
        List<Room> list = new ArrayList<>();

        client.write(Commands.ROOM_LIST, msg);

        int numRooms = Integer.parseInt(client.readLine(0, null).substring(2).trim());

        String room;
        int i = 0;
        while (i < numRooms && (room = client.readLine(2 * 1000, () -> {})) != null){
            Log.println(Log.DEBUG, "SERVER", "RoomString: " + room);
            Scanner roomScanner = new Scanner(room.substring(1));

            long id = roomScanner.nextLong();
            long usersCount = roomScanner.nextLong();
            int  roomColor = roomScanner.nextInt();
            int time = roomScanner.nextInt();
            String roomName = stringInside(room, "[", "]");

            Room r = new Room(roomName, id, time, usersCount, roomColor);
            list.add(r);
            Log.println(Log.DEBUG, "SERVER", "Room Added: " + r.toString());
            ++i;
        }
        return list;
    }

    @Override
    public Room addRoom(Room room) {
        return null;
    }

    @Override
    public String enterRoom(Room room, ChatListener chatListener) throws IOException{
        client.write(Commands.ENTER_IN_ROOM, room.getId() + "");
        String msg = client.readLine(0, null);
        if (msg.charAt(0) == 'e') return null;
        else {
            chatListener.onUserFound(msg.substring(2));
            chatThread = new Thread(() -> chatThreadFun.accept(chatListener));
            chatThread.start();

            return msg.substring(2);
        }
    }

    @Override
    public void sendMessage(String msg) throws IOException {
        client.write(Commands.SEND_MSG, msg);
    }

    @Override
    public void nextUser() throws IOException{
        client.write(Commands.NEXT_USER, "");
    }

    @Override
    public void exitRoom() throws IOException {
        chatting = false;
        exit();
        try {
            System.out.println("JOIN chatThread");
            chatThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ChatThread Terminated");
    }

     @Override
    public void exit() throws IOException{
        client.write(Commands.EXIT, "");
    }


    private static String stringInside(String s, String left, String right){
        Log.println(Log.DEBUG, "ROOM", "Stringa da dividere: " + s);
        if (s == null || s.length() < 3 || !s.contains(left) || !s.contains(right)) return "???";
        else return s.split(Pattern.quote(left))[1].split(Pattern.quote(right))[0];
    }
}
