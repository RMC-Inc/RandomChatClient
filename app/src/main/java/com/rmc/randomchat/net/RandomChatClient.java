package com.rmc.randomchat.net;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class RandomChatClient {

    private static RandomChatClient instance;

    private Socket soc;
    private BufferedReader in;
    private OutputStream out;

    private final String HostName = "2.237.250.35";
    //private final String HostName = "192.168.1.33";
    //private final String HostName = "35.176.132.11"; // AWS
    private final int port = 8125;



    private RandomChatClient(){}

    public static RandomChatClient getInstance(){
        if (instance == null) instance = new RandomChatClient();
        return instance;
    }

    public void openConnection() throws IOException {
        soc = new Socket(HostName, port);
        in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        out = soc.getOutputStream();
    }

    public void closeConnection() throws IOException{
        soc.close();
    }

    public boolean isOpen(){
        return soc != null && !soc.isClosed();
    }

    public void write(char command, String msg) throws IOException {
        synchronized (out){
            out.write((command + " " + msg + "\n").getBytes());
            out.flush();

            Log.println(Log.DEBUG, "SERVER", "Sending: " + command + " " + msg);
        }
    }

    public String readLine() throws IOException{
        return readLine(0);
    }

    public String readLine(int timeout) throws IOException, SocketTimeoutException {
        synchronized (in){
            int prevTimeout = soc.getSoTimeout();
            try {
                Log.println(Log.DEBUG, "READLINE", "Start readline");
                soc.setSoTimeout(timeout);
                String msg = in.readLine();
                if(msg != null){
                    soc.setSoTimeout(prevTimeout);
                    Log.println(Log.DEBUG, "READLINE", "RECEIVED: " + msg);

                    return msg;
                }

            } catch (SocketTimeoutException e){
                e.printStackTrace();
                Log.println(Log.DEBUG, "READLINE", "Timeout");
                soc.setSoTimeout(prevTimeout);
                throw e;
            }
        }
        return null;
    }
}
