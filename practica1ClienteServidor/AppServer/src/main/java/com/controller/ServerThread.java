package main.java.com.controller;

import java.net.Socket;
import java.io.IOException;

public class ServerThread implements Runnable {

    private Socket socket;
    private ComUtils comutils;

    public ServerThread(Socket socket) throws IOException {
        this.socket = socket;
        this.comutils = new ComUtils(this.socket);
    }

    @Override
    public void run() {
        // create input buffer and output buffer
        // wait for input from client and send response back to client
        // close all streams and sockets

        try {
            Protocol protocol = new Protocol(this.socket, this.comutils);

        } catch (IOException exception) {
            System.err.println("Error starting the protocol: " +
                                exception.getMessage());
        }
    }
}
