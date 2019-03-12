package main.java.com.controller;

import java.io.IOException;
import java.net.Socket;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import main.java.com.model.BlackJack;

public class Protocol {
    private Socket socket;
    private BlackJack blackJack;
    private ComUtils comutils;
    private String menssage;
    private int user;

    public Protocol(Socket socket, ComUtils comutils) throws IOException {
        this.socket = socket;
        this.comutils =  comutils;



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
                    //send the exit message and close the rptotocol.
                    break;
                default:
                    System.err.println("It ins't a valid command. closing the game");
                    //set running to false
                    
                    break;
            }
        }
    }

    public int getUser() throws IOException {
        String command = this.comutils.read_string();
        return Integer.parseInt(command.substring(6, command.length()));
    }

    public void sendInit(int chips) throws IOException {
        String message = "INIT ";
        this.comutils.write_string(message);
        this.comutils.write_int32(chips);
        this.readSocket();
    }
}
