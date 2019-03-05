package main.java.com.controller;

import java.io.IOException;
import java.net.Inet4Address;

public class Server {
    private int port;
    private ServerSocket socket;
    private Inet4Address inet4Address;
    private ServerThread clientHandler;
    private Thread thread;

    public Server(Inet4Address inet4Address, int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(inet4Address, port);
        this.socket = serverSocket;
    }

    public void startServer() throws IOException {
        while (true) {
            this.clientHandler = new ServerThread(this.socket.accept());
            this.thread = new Thread(this.clientHandler);
            thread.start();
        }
    }

    protected void finalize() throws IOException {
        this.socket.close();
    }
}
