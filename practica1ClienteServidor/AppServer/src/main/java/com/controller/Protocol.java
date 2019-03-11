package main.java.com.controller;

import java.io.IOException;
import java.net.Socket;
import main.java.com.model.BlackJack;

public class Protocol {
    private Socket socket;
    private BlackJack blackJack;
    private ComUtils comutils;
    private String menssage;

    public Protocol(Socket socket, ComUtils comutils) throws IOException {
        this.socket = socket;
        this.comutils =  comutils;

        this.blackJack = new BlackJack();
        this.readSocket();
    }

    private void readSocket() throws IOException {
        String command = null;
        String message = null;

        while (this.blackJack.isRunning())  {
            command = this.comutils.read_string();
            message = command;
            command = command.toUpperCase();
            command = command.substring(0, 4);

            System.out.println(command);
            switch (command) {
                //Do the switch case for each message in the protocol.
                case "STRT":
                    this.sendInit(message);
                    break;
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

    private void sendInit(String message) {
        if (this.blackJack.getPlayerMoney() > 0) {

        }else {
            //cant start the game not enough money.
        }

    }
}
