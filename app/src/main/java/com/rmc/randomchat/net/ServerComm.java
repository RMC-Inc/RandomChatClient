package com.rmc.randomchat.net;

import java.io.IOException;
import java.util.List;
import com.rmc.randomchat.entity.Room;
import com.rmc.randomchat.entity.User;

// *** Raw lvl blocking functions ***
public interface ServerComm {

    List<Room> getRooms (int numRooms, String roomSearch);
    void setNickname(String nick);

    User enterRoom (long idroom);
    long createRoom (Room room);

    void sendMessage (String message);
    User nextUser();
    List<String> waitMessage() throws IOException;
    List<String> waitMessage(int timeout) throws IOException;

    void sendExit();
}
