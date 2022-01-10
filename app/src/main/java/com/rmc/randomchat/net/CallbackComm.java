package com.rmc.randomchat.net;

import android.os.AsyncTask;

import com.rmc.randomchat.entity.Room;
import com.rmc.randomchat.entity.User;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

public class CallbackComm {
    static ExecutorService executor = Executors.newSingleThreadExecutor();


    public static void getRooms (int numRooms, String roomSearch, Consumer<List<Room>> callback){
        executor.execute(() -> {
            callback.accept(ServerCommImpl.getInstance().getRooms(numRooms, roomSearch));
        });
        ExecutorService e = Executors.newSingleThreadExecutor();
    }

    public static void setNickname(String nick, Runnable callback){
        executor.execute(() -> {
            ServerCommImpl.getInstance().setNickname(nick);
            callback.run();
        });
    }

    public static void enterRoom (int idroom, Consumer<User> callback){
        executor.execute(() -> {
            callback.accept(ServerCommImpl.getInstance().enterRoom(idroom));
        });
    }

    public static void createRoom (Room room, Consumer<Long> callback){
        executor.execute(() -> {
            callback.accept(ServerCommImpl.getInstance().createRoom(room));
        });
    }

    public static void sendMessage (String message, Runnable callback){
        executor.execute(() -> {
            ServerCommImpl.getInstance().sendMessage(message);
            callback.run();
        });
    }

    public static void nextUser(Consumer<User> callback){
        executor.execute(() -> {
            callback.accept(ServerCommImpl.getInstance().nextUser());
        });
    }

    public static void sendExit(Runnable callback){
        executor.execute(() -> {
            ServerCommImpl.getInstance().sendExit();
            callback.run();
        });
    }

}
