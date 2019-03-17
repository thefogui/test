package main.java.com.controller;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import main.java.com.model.Card;
import main.java.com.model.BlackJack;
import main.java.com.model.Users;

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
    private Users users;

    public Protocol(Socket socket, ComUtils comutils, Users users) {
        this.socket = socket;
        this.comutils =  comutils;
        this.command = null;
        this.stringToSend = null;
        this.users = users;
        this.blackJack = new BlackJack(1);
    }

    public void readSocket() throws IOException {
        while (this.blackJack.getIsRunning())  {
            try {

                this.command = this.comutils.readCommand();
                this.command = this.command.toUpperCase();
                switch (this.command) {
                    //Do the switch case for each message in the protocol.
                    case "STRT":
                        this.startAGame();
                        break;
                    case "CASH":
                        this.setCash();
                        break;
                    case "HITT":
                        this.askExtraCard();
                        break;
                    case "SHOW":
                        this.showCards();

                        this.solveWinner();
                        break;
                    case "BETT":
                        try {
                            this.doubleBet();
                        } catch (Exception e) {
                            System.err.println("Error while double the bet");
                        }
                        break;
                    case "SRND":
                        this.surrender();
                        break;
                    case "RPLY":
                        this.startNewGame();
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

    private void startAGame() throws IOException{
        String space = this.comutils.read_Char();
        int userId = this.comutils.read_int32();

        this.users.addNewUser(userId);
        this.sendInit(100);
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

    private void startNewGame() throws IOException {
        this.comutils.read_Char();

    }

    private void surrender() throws IOException {
        if (this.blackJack.getPlayerMoney() <= 0) {
            this.message = "ERRO";
            this.comutils.writeCommand(this.message);
            this.comutils.writeErrorMessage("Not enough money!");
        } else {
            this.sendWINS();
        }
    }

    private void doubleBet() throws Exception {
        if (this.blackJack.getRoundCount() < this.blackJack.getPlayerHand().getActualValue()) {
            this.stringToSend = "ERRO";
            this.comutils.writeCommand(this.stringToSend);
            this.stringToSend = "Not enough money to double the bet";
            this.comutils.writeErrorMessage(this.stringToSend);
        }
        this.blackJack.doubleBet();
    }

    private void showCards() throws IOException {
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

    private void askExtraCard() throws IOException {
        this.sendCARD();
    }

    private void setCash() throws IOException {
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

    public void sendInit(int chips) throws IOException {
        String message = "INIT";
        this.comutils.writeCommand(message);
        this.comutils.write_SP();
        this.comutils.write_int32(chips);
    }
}
