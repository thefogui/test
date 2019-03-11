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
        String command =  null;

        while (this.blackJack.isRunning())  {
            command = this.comutils.read_string();
            command = command.toUpperCase();

            switch (command) {
                //Do the switch case for each message in the protocol.
            }
        }
    }
}
