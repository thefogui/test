package main.java.com.controller;

import java.io.IOException;
import java.net.Inet4Address;

public class Mainthread {
    private int port;
    private ServerSocket socket;
    private Inet4Address inet4Address;
    private ServerThread clientHandler;
    private Thread thread;

    public Mainthread(int port) throws IOException {
        this.port = port;
        ServerSocket serverSocket = new ServerSocket(inet4Address, this.port);
        this.socket = serverSocket;
    }

    public void startServer() throws IOException {
        while (true) {
            System.out.println("Waiting for clients...");
            this.clientHandler = new ServerThread(this.socket.accept());
            this.thread = new Thread(this.clientHandler);
            thread.start();
        }
    }

    protected void finalize() throws IOException {
        this.socket.close();
    }
}
