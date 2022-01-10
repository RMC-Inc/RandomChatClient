package com.rmc.randomchat.net;

import android.os.AsyncTask;
import android.telecom.Call;

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

    private static boolean chatting = false;
    private static Consumer<String> onNewMsg = (s) -> {};
    private static Runnable onExit = () -> {};
    private static Runnable onNext = () -> {};

    private CallbackComm(){}


    public static void startChatting(){
        chatting = true;

        ExecutorService chatExecutor = Executors.newSingleThreadExecutor();
        chatExecutor.execute(() -> {
            while (chatting){
                String msg = ServerCommImpl.getInstance().waitMessage();

                if(msg == null || msg.length() == 0){
                    chatting = false;
                    onExit.run();
                    return;
                }

                if (msg.charAt(0) == Commands.SEND_MSG){
                    onNewMsg.accept(msg);
                } else if(msg.charAt(0) == Commands.NEXT_USER){
                    chatting = false;
                    onNext.run();
                } else if(msg.charAt(0) == Commands.EXIT){
                    chatting = false;
                    onExit.run();
                }
            }
        });
    }

    public static Consumer<String> getOnNewMsg() {
        return onNewMsg;
    }

    public static void setOnNewMsg(Consumer<String> onNewMsg) {
        CallbackComm.onNewMsg = onNewMsg;
    }

    public static Runnable getOnExit() {
        return onExit;
    }

    public static void setOnExit(Runnable onExit) {
        CallbackComm.onExit = onExit;
    }

    public static Runnable getOnNext() {
        return onNext;
    }

    public static void setOnNext(Runnable onNext) {
        CallbackComm.onNext = onNext;
    }

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

    public static void enterRoom (long idroom, Consumer<User> callback){
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
            chatting = false;
            callback.accept(ServerCommImpl.getInstance().nextUser());
        });
    }

    public static void sendExit(Runnable callback){
        executor.execute(() -> {
            chatting = false;
            ServerCommImpl.getInstance().sendExit();
            callback.run();
        });
    }

}
