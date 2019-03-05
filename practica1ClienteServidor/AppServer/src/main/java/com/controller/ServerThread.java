package main.java.com.controller;

import java.net.Socket;

public class ServerThread implements Runnable {

    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // create input buffer and output buffer
        // wait for input from client and send response back to client
        // close all streams and sockets
    }
}
