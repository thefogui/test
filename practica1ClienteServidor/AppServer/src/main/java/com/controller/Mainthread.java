package main.java.com.controller;


import main.java.com.model.Users;
import java.io.IOException;
import java.net.Inet4Address;

public class Mainthread {
    private int port;
    private ServerSocket socket;
    private Inet4Address inet4Address;
    private ServerThread clientHandler;
    private Users users;

    public Mainthread(int port) throws IOException {
        this.port = port;
        ServerSocket serverSocket = new ServerSocket(inet4Address, this.port);
        this.socket = serverSocket;
        this.users = new Users();
    }

    public void startServer() throws IOException {
        while (true) {
            System.out.println("Waiting for clients...");
            this.clientHandler = new ServerThread(this.socket.accept(), this.users);
            Thread thread = new Thread(this.clientHandler);
            thread.start();
        }
    }

    protected void finalize() throws IOException {
        this.socket.close();
    }
}
