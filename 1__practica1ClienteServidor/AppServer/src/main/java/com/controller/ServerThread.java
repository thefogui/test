package main.java.com.controller;

import java.net.Socket;
import java.io.IOException;
import main.java.com.model.Users;

/**
 * Class that starts the protocol and creates a new thread
 *
 * @author Vitor Carvalho and Ivet Aymerich
 */
public class ServerThread implements Runnable {
    private Protocol protocol;
    private Users users;

    /**
     * Cinstructor, creates a new protocol object
     * @param socket the socket used in the communication
     * @param users HashMap with all users
     * @throws IOException error creating the socket.
     */
    public ServerThread(Socket socket, Users users) throws IOException {
        this.users = users;
        this.protocol = new Protocol(socket, this.users);
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
