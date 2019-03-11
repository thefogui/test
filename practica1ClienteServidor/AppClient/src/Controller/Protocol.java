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

    public Protocol(String server, int port) throws IOException {
        this.serverAddress = InetAddress.getByName(server);
        this.socket = new Socket(this.serverAddress, port);
        this.comUtils = new ComUtils(this.socket);
    }

    public void start() throws IOException {
        //Start the game here and connect to server
        this.comUtils.write_string("STRT");
    }

    public void readSocket() throws Exception {
        String message = null;
        while(this.blackJack.isRunning()) {
            message = this.comUtils.read_string(); //read socket
            message = message.toUpperCase();

            switch (message) {
                case "CASH":
                    break;
                case "HITT":
                    break;
                case "SHOW":
                    break;
                case "BETT":
                    break;
                case "SRND":
                    break;
                case "RPLY":
                    break;
                case "EXIT":
                    break;
                default:
                    System.err.println("It ins't a valid command. closing the game");
                    //set running to false
                    break;
            }
        }
    }
}