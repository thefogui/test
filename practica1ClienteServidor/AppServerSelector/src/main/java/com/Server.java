package main.java.com;

import java.io.IOException;

public class Server {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Invalid entry for port");
            System.exit(1);
        }

        try {
            main.java.com.controller.ServerSelector serverSelector = new main.java.com.controller.ServerSelector(Integer.parseInt(args[1]));
            serverSelector.startServer();
        }catch (IOException e) {
            System.err.println("Error: can't start main thread: " + e.getMessage());
        }
    }
}