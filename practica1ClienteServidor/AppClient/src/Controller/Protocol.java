package Controller;

import java.io.*;
import java.net.*;
import lib.*;

public class Protocol {
    private ComUtils comUtils;
    private Socket socket;
    private InetAddress serverAddress;
    private BlackJack blackJack;
    private String message;

    public Protocol(String server, int port, int username) throws IOException {
        this.serverAddress = InetAddress.getByName(server);
        this.socket = new Socket(this.serverAddress, port);
        this.comUtils = new ComUtils(this.socket);
        this.blackJack = new BlackJack(username);
    }

    public void start() throws IOException {
        //Start the game here and connect to server
        this.message = "STRT " + this.blackJack.getPlayerID();
        this.comUtils.write_string("STRT");
        this.readSocket();
    }

    public void readSocket() throws IOException {
        String message = null;
        while(this.blackJack.isRunning()) {
            message = this.comUtils.read_string(); //read socket
            message = message.toUpperCase();

            switch (message) {
                case "INIT":
                    this.startTheGame();

                    break;
                case "IDCK":
                    break;
                case "CARD":
                    break;
                case "SHOW":
                    break;
                case "WINS":
                    break;
                default:
                    System.err.println("It ins't a valid command. closing the game");
                    //set running to false
                    break;
            }
        }
    }

    private void startTheGame() {

    }
}