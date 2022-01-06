package com.rmc.randomchat.net;

import java.util.List;
import com.rmc.randomchat.entity.Room;

public interface ServerComm {

    List<Room> getRooms (int numRooms, String roomSearch);
    void setNickname(String nick);

    void enterRoom (int idroom);
    void createRoom (Room room);

    void sendMessage (String message);
    void nextUser ();
    void closeConnection();
}
