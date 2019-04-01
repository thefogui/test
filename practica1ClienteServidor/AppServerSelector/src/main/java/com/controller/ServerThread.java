package main.java.com.controller;

import com.sun.security.ntlm.Client;
import main.java.com.Server;
import main.java.com.model.Users;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public class ServerThread implements Runnable {

    private Protocol protocol;
    private Users users;
    private int userIndex;

    public ServerThread (SocketChannel socket, Users users) throws IOException {
        this.users = users;
        this.userIndex = Integer.MAX_VALUE;
        this.protocol =  new Protocol(socket, this.users);
        this.run();
    }

    @Override
    public void run() {
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
