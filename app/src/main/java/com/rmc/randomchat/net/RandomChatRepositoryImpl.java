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
            while (chatting || msg.charAt(0) != 'x'){
                msg = client.readLine(0, null);

                switch (msg.charAt(0)){
                    case Commands.ENTER_IN_ROOM:
                        chatListener.onUserFound(stringInside(msg, "[", "]"));
                        break;
                    case Commands.SEND_MSG:
                        chatListener.onMessage(msg.substring(2));
                        break;
                    case Commands.NEXT_USER:
                        chatListener.onNextUser();
                        break;
                    case Commands.TIME_EXPIRED:
                        chatListener.onTimeExpired();
                        break;
                    case Commands.EXIT:
                        chatListener.onExit();
                        break;
                    case Commands.USERS_IN_ROOM:
                        chatListener.onUsersCount(Long.parseLong(msg.substring(2).trim()));
                        break;
                    case Commands.EXIT_FROM_ROOM:
                        return;
                    default:
                        throw new IllegalStateException("Unexpected value: " + msg.charAt(0));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    Thread chatThread = null;

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

    private List<Room> getRooms(int from, int to, String name) throws IOException {
        if(chatThread != null)
            try{
                chatThread.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }

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
    public Room addRoom(Room room) throws IOException{
        String msg = room.getRoomColor() + " " + room.getTime() + " [" + room.getName() + "]";
        client.write(Commands.NEW_ROOM, msg);

        String roomStr = client.readLine(10 * 1000, null);
        if (roomStr == null) return null;
        else return new Room(room.getName(), Long.parseLong(roomStr.substring(2).trim()), room.getTime(), 0, room.getRoomColor());
    }

    @Override
    public String enterRoom(Room room, ChatListener chatListener) throws IOException, RoomNotExistsException {
        client.write(Commands.ENTER_IN_ROOM, room.getId() + "");

        do{
            String msg = client.readLine(0, null);
            if(msg != null){
                if (msg.charAt(0) == Commands.EXIT) throw new RoomNotExistsException();
                if(msg.charAt(0) == Commands.EXIT_FROM_ROOM) return null;

                if(msg.charAt(0) == Commands.USERS_IN_ROOM) chatListener.onUsersCount(Long.parseLong(msg.substring(2).trim()));
                else {
                    chatListener.onUserFound(stringInside(msg, "[", "]"));
                    chatThread = new Thread(() -> chatThreadFun.accept(chatListener));
                    chatting = true;
                    chatThread.start();

                    return msg.substring(2);
                }
            }
        } while (true);

    }

    @Override
    public void sendMessage(String msg) throws IOException {
        client.write(Commands.SEND_MSG, msg.replace("\n", " "));
    }

    @Override
    public void nextUser() throws IOException{
        client.write(Commands.NEXT_USER, "");
    }

    @Override
    public void exitRoom() throws IOException {
        if(!chatting){
            System.out.println("Sending Exit");
            exit();
        } else {
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
        System.out.println("Exit From Room.");
    }

     @Override
    public void exit() throws IOException{
        client.write(Commands.EXIT, "");
    }

    @Override
    public void getUserCount() throws IOException{
        client.write(Commands.USERS_IN_ROOM, "");
    }


    private static String stringInside(String s, String left, String right){
        Log.println(Log.DEBUG, "ROOM", "Stringa da dividere: " + s);
        if (s == null || s.length() < 3 || !s.contains(left) || !s.contains(right)) return "???";
        else return s.split(Pattern.quote(left))[1].split(Pattern.quote(right))[0];
    }
}
