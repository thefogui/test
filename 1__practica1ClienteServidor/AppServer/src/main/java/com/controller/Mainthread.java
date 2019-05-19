package main.java.com.controller;


import main.java.com.model.Users;
import java.io.IOException;
import java.net.*;

/**
 * Mainthread class it  start the server and waits to clients connections.
 *
 * @author Vitor Carvalho and Ivet Aymerich.
 */
public class Mainthread {
    private int port;
    private ServerSocket socket;
    private ServerThread clientHandler;
    private Users users;

    /**
     * Constructor of the class
     * @param port port that the server is allocated
     * @throws IOException error creating the socket.
     */
    public Mainthread(int port) throws IOException {
        this.port = port;
        ServerSocket serverSocket = new ServerSocket(this.port);
        this.socket = serverSocket;
        this.users = new Users();
    }

    public void startServer() throws IOException {
        while (true) {
            System.out.println("Waiting for clients...");
            this.clientHandler = new ServerThread(this.socket.accept(), this.users); //accept a connection and starts
                                                                                    //a new thread
            Thread thread = new Thread(this.clientHandler);
            thread.start();
        }
    }

    protected void finalize() throws IOException {
        this.socket.close();
    }
}
