package main.java.com.controller;

import com.sun.tools.javac.util.ArrayUtils;
import main.java.com.model.BlackJack;

import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

public class ServerThread implements Runnable {

    private Socket socket;
    private ComUtils comutils;
    private ArrayList<BlackJack> users;
    private int userIndex;

    public ServerThread(Socket socket) throws IOException {
        this.socket = socket;
        this.comutils = new ComUtils(this.socket);

        this.users = new ArrayList<>();

        this.userIndex = Integer.MAX_VALUE;
    }

    @Override
    public void run() {
        // create input buffer and output buffer
        // wait for input from client and send response back to client
        // close all streams and sockets

        try {
            Protocol protocol = new Protocol(this.socket, this.comutils);
            //check if the user is playing if is the case check his coins
            //otherwise put it in the users list
            int userID = protocol.getUser();

            if (checkUser(userID)) {
                //get the user actual money
                if (this.users.get(this.userIndex).getPlayerMoney() > 0) {
                    protocol.sendInit(this.users.get(this.userIndex).getPlayerMoney());
                } else {
                    throw new Exception("This user has no money available");
                }
            } else {
                //create the new user.
                BlackJack blackJack = new BlackJack(userID);
                this.users.add(blackJack);
                protocol.sendInit(20);
            }

        } catch (IOException exception) {
            System.err.println("Error starting the protocol: " +
                                exception.getMessage());
        } catch (Exception e) {
            System.err.println("Error starting the protocol: " + e.getMessage());
        }
    }

    private boolean checkUser(int userID) {

        for(BlackJack user: this.users) {
            if (user.getPlayerName() == userID) {
                this.userIndex = this.users.indexOf(user);
                return true;
            }
        }
        this.userIndex = Integer.MAX_VALUE;
        return false;
    }
}
