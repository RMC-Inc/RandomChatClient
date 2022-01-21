package com.rmc.randomchat.net;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Server {

    private static Server instance;

    private Socket soc;
    private InputStream in;
    private OutputStream out;

    private final String HostName = "2.237.250.35";
    private final int port = 8125;

    private byte[] buff = new byte[500];



    private Server(){}

    public static Server getInstance(){
        if (instance == null) instance = new Server();
        return instance;
    }

    public void openConnection() throws IOException {
        soc = new Socket(HostName, port);
        in = soc.getInputStream();
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

    public List<String> read(int timeout) throws IOException {
        synchronized (in){
            int prevTimeout = 0;
            prevTimeout = soc.getSoTimeout();
            soc.setSoTimeout(timeout);

            int len = in.read(buff);
            Log.println(Log.DEBUG, "SERVER", "Strlen: " + len);
            soc.setSoTimeout(prevTimeout);

            if (len <= 0) return null;

            String msg = new String(buff).substring(0, len);
            return Arrays.asList(msg.split("\n"));
        }
    }
}
