package main.java.com;

import main.java.com.controller.Mainthread;

import java.io.IOException;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        /*if (args.length < 1) {
            System.err.println("Invalid entry for port");
            System.exit(1);
        }*/
        System.out.println("Port");
        Scanner sc = new Scanner(System.in);

        try {
            Mainthread mainthread = new Mainthread(sc.nextInt());
            mainthread.startServer();
        }catch (IOException e) {
            System.err.println("Error: can't start main thread: " + e.getMessage());
        }


    }
}
