package Controller;

import java.io.*;
import java.net.*;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
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
        this.comUtils.write_string(this.message);
        this.comUtils.write_SP();
        this.comUtils.write_int32(this.blackJack.getPlayerID());
    }

    public void sendCash(int chips) throws IOException {
        this.message = "CASH";
        this.comUtils.write_string(this.message);
        this.comUtils.write_SP();
        this.comUtils.write_int32(chips);
    }

    public void sendHitt() throws IOException {
        this.message = "HITT";
        this.comUtils.write_string(this.message);
    }

    public void sendShow() throws IOException {
        int length = this.blackJack.getDealerHand().getHandCards().size();
        this.message = "SHOW";
        this.comUtils.write_string(this.message);
        this.comUtils.write_SP();

        this.comUtils.writeLen((char)length);

        for(Card card : this.blackJack.getDealerHand().getHandCards()) {
            this.comUtils.write_SP();
            this.comUtils.writeCard(card.getRank(), card.getCardNaipe());
        }
    }

    public void sendBet() throws IOException {
        this.message = "BETT";
        this.comUtils.write_string(this.message);
    }

    public void sendSurrender() throws IOException {
        this.message = "SRND";
        this.comUtils.write_string(this.message);
    }

    public void sendReplay() throws IOException {
        this.message = "RPLY";
        this.comUtils.write_string(this.message);
    }


    public String handlerError() throws IOException {
        String space = this.comUtils.read_Char();;
        return this.comUtils.read_string_variable(99);
    }

    public String takeACard() throws IOException {
        String space = this.read_sp();
        String naipe, rank;
        rank = this.comUtils.read_Char();
        naipe = this.comUtils.read_Char();;
        Card card = new Card(naipe.charAt(0), rank.charAt(0));
        this.blackJack.getPlayerHand().take(card);
        return card.toString();
    }

    public String takeDealerCard() throws IOException {
        String space = this.read_sp();
        int length = this.comUtils.readLen();
        space = this.read_sp();
        String naipe, rank;
        rank = this.comUtils.read_Char();
        naipe = this.comUtils.read_Char();
        Card card = new Card(naipe.charAt(0), rank.charAt(0));
        return card.toString();
    }

    public String readCommand() throws IOException {
        return this.comUtils.readCommand();
    }

    public String read_sp() throws IOException {
        return this.comUtils.read_Char();
    }

    public boolean getIsRunning() {
        return this.blackJack.getIsRunning();
    }

    public String handAmount() {
        return String.valueOf(this.blackJack.getPlayerHand().getActualValue());
    }

    public int readInteger() throws IOException {
        return this.comUtils.read_int32();
    }

    public String checkWinner() throws IOException{
        String space = this.read_sp();
        String winner = this.comUtils.read_Char();
        space = this.read_sp();
        int amount = this.comUtils.read_int32();

        switch (winner) {
            case "0":
                return "You are the winner, cash earned: " + amount;
            case "1":
                return "Dealer is the winner, cash lost: " + amount;
            case "2":
                return "Tie, no ones wins the bet";
            default:
                this.sendError("There is an error identifying the winner, closing connection!");
                //close the connection
                break;
        }
        return null;
    }

    public String getServerCards() throws IOException {
        String cards = "";
        String space = this.read_sp();
        int length = this.comUtils.readLen();
        String naipe, rank;

        for (int i = 0; i < length; i++) {
            space = this.read_sp();
            rank = this.comUtils.read_Char();
            naipe = this.comUtils.read_Char();
            Card card = new Card(naipe.charAt(0), rank.charAt(0));
            cards += card.toString() + " ";
        }
        return cards;
    }

    private void sendError(String error) throws IOException {
        this.message = "ERRO";
        this.comUtils.writeCommand(this.message);
        this.comUtils.write_SP();
        this.comUtils.writeErrorMessage(error);
    }

    public int getPlayerBet() {
        return this.blackJack.getPlayerBet();
    }

    public void sendExit() throws IOException {
        this.message = "EXIT";
        this.comUtils.writeCommand(this.message);
    }

    public void setPlaying(boolean b) {
        this.blackJack.setRunning(b);
    }
}

