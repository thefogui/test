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
        this.message = "STRT";
        this.comUtils.writeCommand(this.message);
        this.comUtils.write_SP();
        this.comUtils.write_int32(this.blackJack.getPlayerID());
        this.readSocket();
    }

    public void readSocket() throws IOException {
        while(this.blackJack.isRunning()) {
            this.message = this.comUtils.read_string(); //read socket
            this.message = this.message.toUpperCase();

            switch (this.message) {
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

    public void sendCash(int chips) throws IOException {
        this.message = "CASH";
        this.comUtils.writeCommand(this.message);
        this.comUtils.write_SP();
        this.comUtils.write_int32(chips);
    }

    public void sendHitt() throws IOException {
        this.message = "HITT";
        this.comUtils.writeCommand(this.message);
    }

    public void sendShow() throws IOException {
        int lenght = this.blackJack.getDealerHand().getHandCards().size();
        this.message = "SHOW";
        this.comUtils.writeCommand(this.message);
        this.comUtils.write_SP();

        this.comUtils.writeLen(lenght);

        for(Card card : this.blackJack.getDealerHand().getHandCards()) {
            this.comUtils.write_SP();
            this.comUtils.writeCard(card.getRank(), (byte) card.getCardNaipe());
        }
    }

    public void sendBet() throws IOException {
        this.message = "BETT";
        this.comUtils.writeCommand(this.message);
    }

    public void sendSurrender() throws IOException {
        this.message = "SNRD";
        this.comUtils.writeCommand(this.message);
    }

    public void sendReplay() throws IOException {
        this.message = "RPLY";
        this.comUtils.writeCommand(this.message);
    }

    private void startTheGame() {

    }
}