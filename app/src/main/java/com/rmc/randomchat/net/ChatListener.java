package com.rmc.randomchat.net;

public interface ChatListener {
    void onUserFound(String msg);
    void onMessage(String msg);
    void onNextUser();
    void onTimeExpired();
    void onExit();
    void onUsersCount(long usersCount);
}
