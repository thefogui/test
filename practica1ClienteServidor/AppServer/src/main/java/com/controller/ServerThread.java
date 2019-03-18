package main.java.com.controller;

import java.net.Socket;
import java.io.IOException;
import main.java.com.model.Users;

public class ServerThread implements Runnable {
    private Socket socket;
    private ComUtils comutils;

    private int userIndex;
    private Protocol protocol;
    private int userID;
    private Users users;

    public ServerThread(Socket socket, Users users) throws IOException {
        this.socket = socket;
        this.comutils = new ComUtils(this.socket);
        this.users = users;
        this.userIndex = Integer.MAX_VALUE;
        this.protocol =  new Protocol(this.socket, this.comutils, this.users);
    }

    @Override
    public void run() {
        // create input buffer and output buffer
        // wait for input from client and send response back to client
        // close all streams and sockets

        try {
            //check if the user is playing if is the case check his coins
            //otherwise put it in the users list
            this.protocol.readSocket();

        } catch (IOException exception) {
            System.err.println("Error starting the protocol: " +
                                exception.getMessage());
        } catch (Exception e) {
            System.err.println("Error starting the protocol: " + e.getMessage());
        }
    }
}
