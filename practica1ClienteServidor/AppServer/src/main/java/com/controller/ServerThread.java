package main.java.com.controller;

import main.java.com.model.BlackJack;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ServerThread implements Runnable {
    private Socket socket;
    private ComUtils comutils;
    private ArrayList<BlackJack> users;
    private int userIndex;
    private Protocol protocol;
    private int userID;

    public ServerThread(Socket socket) throws IOException {
        this.socket = socket;
        this.comutils = new ComUtils(this.socket);
        this.users = new ArrayList<>();
        this.userIndex = Integer.MAX_VALUE;
        this.protocol =  new Protocol(this.socket, this.comutils);
    }

    @Override
    public void run() {
        // create input buffer and output buffer
        // wait for input from client and send response back to client
        // close all streams and sockets

        try {
            //check if the user is playing if is the case check his coins
            //otherwise put it in the users list
            this.userID = this.protocol.getUser();

            if (checkUser(this.userID)) {

                //get the user actual money
                if (this.users.get(this.userIndex).getPlayerMoney() > 0) {
                    this.protocol.sendInit(this.users.get(this.userIndex).getPlayerMoney());

                } else
                    throw new Exception("This user has no money available");
            } else {
                //create the new user.
                BlackJack blackJack = new BlackJack(this.userID);
                this.users.add(blackJack);
                this.protocol.sendInit(500);
            }
        } catch (IOException exception) {
            System.err.println("Error starting the protocol: " +
                                exception.getMessage());
        } catch (Exception e) {
            System.err.println("Error starting the protocol: " + e.getMessage());
        }
    }

    private boolean checkUser(int userID) {

        Iterator iter = this.users.iterator();

        while(iter.hasNext()) {
            BlackJack user = (BlackJack) iter.next();

            if (user.getPlayerName() == userID) {
                this.userIndex = this.users.indexOf(user);
                return true;
            }
        }
        this.userIndex = Integer.MAX_VALUE;
        return false;
    }
}
