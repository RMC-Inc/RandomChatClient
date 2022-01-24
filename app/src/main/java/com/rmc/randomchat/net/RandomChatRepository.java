package com.rmc.randomchat.net;

import com.rmc.randomchat.entity.Room;

import java.io.IOException;
import java.util.List;

public interface RandomChatRepository{

    void connect() throws IOException;

    void setNickname(String nickname) throws IOException;

    List<Room> getAllRooms() throws IOException;
    List<Room> getRooms(int from, int to) throws IOException;
    List<Room> getRoomsByName(String name) throws IOException;
    Room addRoom(Room room) throws IOException ;

    String enterRoom(Room room, ChatListener chatListener) throws IOException, RoomNotExistsException;

    void sendMessage(String msg) throws IOException;
    void nextUser() throws IOException;
    void exitRoom() throws IOException;

    void exit() throws IOException;
}
