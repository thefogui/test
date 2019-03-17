package main.java.com.controller;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import main.java.com.model.Card;
import main.java.com.model.BlackJack;

public class Protocol {
    private Socket socket;
    private BlackJack blackJack;
    private ComUtils comutils;
    private String message;
    private String lastCommandSent;
    private int user;
    private String command;
    private String stringToSend;
    private int intToSend;

    public Protocol(Socket socket, ComUtils comutils) throws IOException {
        this.socket = socket;
        this.comutils =  comutils;
        this.command = null;
        this.stringToSend = null;
    }

    private void readSocket() throws IOException {
        while (this.blackJack.getIsRunning())  {
            try {
                String message = null;
                this.command = this.comutils.readCommand();
                this.command = this.command.toUpperCase();
                this.command = this.command.substring(0, 4);

                switch (this.command) {
                    //Do the switch case for each message in the protocol.
                    case "CASH":
                        this.setCash(message);
                        break;
                    case "HITT":
                        this.askExtraCard(message);
                        break;
                    case "SHOW":
                        this.showCards(message);

                        this.solveWinner();
                        break;
                    case "BETT":
                        try {
                            this.doubleBet(message);
                        } catch (Exception e) {
                            System.err.println("Error while double the bet");
                        }
                        break;
                    case "SRND":
                        this.surrender(message);
                        break;
                    case "RPLY":
                        this.startNewGame(message);
                        break;
                    case "EXIT":
                        //send the exit message and close the prtotocol.
                        System.out.println("Closing the connection.");
                        this.socket.close();
                    /*try {
                        this.finalize();
                    } catch (Throwable exception) {
                        exception.printStackTrace();
                    }*/
                        break;
                    case "ERRO":
                        System.err.println(this.handlerError());
                        break;
                    default:
                        System.err.println("It ins't a valid command. closing the game");
                        //set running to false
                        break;
                }
            }catch (SocketException ex) {
                System.err.println("La conexión con el cliente se ha interrumpido");
            } catch (IOException ex){
                throw ex;
            }
        }
    }

    private void solveWinner() throws IOException {
        this.blackJack.dealerAskCard();
        if (this.blackJack.getPlayerBet() == 1) {
            this.sendWINS();
        } else {
            this.blackJack.dealPlayerCard();
        }
        this.sendSHOW();
    }

    public String handlerError() throws IOException {
        return this.comutils.readErrorMessage();
    }

    public void sendIDCK() throws IOException {
        this.stringToSend = "IDCK";
        this.comutils.writeCommand(this.stringToSend);
        for(Card card : this.blackJack.getPlayerHand().getHandCards()){
            this.comutils.write_SP();
            this.comutils.writeCard(card.getRank(), (byte) card.getCardNaipe());
        }
    }

    public void sendCARD()  throws IOException {
        this.stringToSend = "CARD";
        this.comutils.writeCommand(this.stringToSend);
        this.comutils.write_SP();
        Card card = this.blackJack.dealPlayerCard();
        this.comutils.writeCard(card.getRank(), (byte) card.getCardNaipe());
    }

    public void sendSHOW() throws IOException {
        int lenght = this.blackJack.getDealerHand().getHandCards().size();
        this.stringToSend = "SHOW";
        this.comutils.writeCommand(this.stringToSend);
        this.comutils.write_SP();

        this.comutils.writeLen(lenght);

        for(Card card : this.blackJack.getDealerHand().getHandCards()) {
            this.comutils.write_SP();
            this.comutils.writeCard(card.getRank(), (byte) card.getCardNaipe());
        }
    }

    public void sendWINS() throws IOException {
        this.stringToSend = "WINS";
        this.comutils.writeCommand(this.stringToSend);
        this.comutils.write_SP();

        this.comutils.writeLen(this.blackJack.getWinner());
        this.comutils.write_SP();
        this.comutils.write_int32(this.blackJack.getRoundCount()); //get the amount of chips earned
    }

    private void startNewGame(String message) throws IOException {
        this.comutils.read_Char();

    }

    private void surrender(String message) throws IOException {
        if (this.blackJack.getPlayerMoney() <= 0) {
            this.message = "ERRO";
            this.comutils.writeCommand(this.message);
            this.comutils.writeErrorMessage("Not enough money!");
        } else {
            this.sendWINS();
        }
    }

    private void doubleBet(String message) throws Exception {
        if (this.blackJack.getRoundCount() < this.blackJack.getPlayerHand().getActualValue()) {
            this.stringToSend = "ERRO";
            this.comutils.writeCommand(this.stringToSend);
            this.stringToSend = "Not enough money to double the bet";
            this.comutils.writeErrorMessage(this.stringToSend);
        }
        this.blackJack.doubleBet();
    }

    private void showCards(String message) throws IOException {
        int length;
        String rank;
        String suit;

        this.comutils.read_Char();
        length = this.comutils.readLen();

        for (int i = 0; i < length; i++) {
            this.comutils.read_Char();
            rank = this.comutils.read_Char();
            suit = this.comutils.read_Char();
            this.blackJack.getPlayerHand().take(rank.charAt(0), suit.charAt(0));
        }
    }

    private void askExtraCard(String message) throws IOException {
        this.sendCARD();
    }

    private void setCash(String message) throws IOException {
        int cash;
        this.comutils.read_Char();
        cash = this.comutils.read_int32();

        if (cash < this.blackJack.MAX_BET){
            this.message = "ERRO";
            this.comutils.writeCommand(this.message);
            this.comutils.writeErrorMessage("The cash should be greater than 100");
        } else {
            this.blackJack.setPlayerMoney(cash);
            this.sendIDCK();
        }
    }

    public int getUser() throws IOException {
        this.command = this.comutils.read_string();
        int userId = this.comutils.read_int32();
        return userId;
    }

    public void sendInit(int chips) throws IOException {
        String message = "INIT";
        this.comutils.writeCommand(message);
        this.comutils.write_SP();
        this.comutils.write_int32(chips);
        this.readSocket();
    }
}
