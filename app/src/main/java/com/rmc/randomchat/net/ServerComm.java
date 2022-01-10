package com.rmc.randomchat.net;

import java.util.List;
import com.rmc.randomchat.entity.Room;
import com.rmc.randomchat.entity.User;

// *** Raw lvl blocking functions ***
public interface ServerComm {
    List<Room> getRooms (int numRooms, String roomSearch);
    void setNickname(String nick);

    User enterRoom (int idroom);
    long createRoom (Room room);

    void sendMessage (String message);
    User nextUser();
    String waitMessage();

    void sendExit();
}
