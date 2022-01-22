package com.rmc.randomchat.net;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server {

    private static Server instance;

    private Socket soc;
    private BufferedReader in;
    private OutputStream out;

    private final String HostName = "2.237.250.35";
    //private final String HostName = "192.168.1.26";
    private final int port = 8125;



    private Server(){}

    public static Server getInstance(){
        if (instance == null) instance = new Server();
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

    public String read(int timeout, Runnable onTimeout) throws IOException {
        synchronized (in){
            int prevTimeout = soc.getSoTimeout();
            try {
                soc.setSoTimeout(timeout);
                String msg = in.readLine();
                soc.setSoTimeout(prevTimeout);

                return msg;

            } catch (SocketTimeoutException e){
                if (onTimeout != null) onTimeout.run();
                else e.printStackTrace();
            } finally {
                soc.setSoTimeout(prevTimeout);
            }
        }
        return null;
    }
}
