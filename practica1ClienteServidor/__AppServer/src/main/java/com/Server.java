package main.java.com;

import main.java.com.controller.Mainthread;

import java.io.IOException;

public class Server {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Invalid entry for port");
            System.exit(1);
        }

        try {
            Mainthread mainthread = new Mainthread(Integer.parseInt(args[1]));
            mainthread.startServer();
        }catch (IOException e) {
            System.err.println("Error: can't start main thread: " + e.getMessage());
        }


    }
}
