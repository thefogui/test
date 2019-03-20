package main.java.com.controller;

import java.io.IOException;
import java.net.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import main.java.com.model.Card;
import main.java.com.model.BlackJack;
import main.java.com.model.Users;


public class Protocol {
    private Socket socket;
    private BlackJack blackJack;
    private ComUtils comutils;
    private String message;
    private String command;
    private String stringToSend;
    private Users users;
    private boolean firstCash;
    static FileHandler file;


    public Protocol(Socket socket, Users users) throws IOException {
        this.command = null;
        this.stringToSend = null;
        this.users = users;
        this.blackJack = new BlackJack(1);
        this.firstCash = true;
        this.socket = socket;
        this.comutils = new ComUtils(this.socket);
    }

    public void readSocket() throws IOException {
        while (this.blackJack.getIsRunning())  {
            try {

                this.command = this.comutils.readCommand();
                this.command = this.command.toUpperCase();
                System.out.println(command);
                this.writeLog("CLIENTE: " + command);
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
                        this.blackJack.dealerAskCard();
                        this.solveWinner();
                        break;
                    case "BETT":
                        try {
                            this.doubleBet();
                        } catch (Exception e) {
                            System.err.println("Error while double the bet");
                            this.blackJack.setRunning(false);
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
                        this.blackJack.setRunning(false);
                        this.socket.close();
                        /*try {
                            this.finalize();
                        } catch (Throwable exception) {
                            exception.printStackTrace();
                        }*/
                        break;
                    case "ERRO":
                        System.err.println(this.handlerError());
                        this.blackJack.setRunning(false);
                        break;
                    default:
                        System.err.println("It ins't a valid command. closing the game");
                        //set running to false
                        this.blackJack.setRunning(false);
                        break;
                }
            }catch (SocketException ex) {
                System.err.println("La conexión con el cliente se ha interrumpido");
                this.blackJack.setRunning(false);
            } catch (IOException ex){
                throw ex;
            }
        }
    }

    private void startAGame() throws IOException{
        String space = this.comutils.read_Char();
        int userId = this.comutils.read_int32();
        this.writeLog("CLIENTE: " + userId);
        System.out.println(userId);
        this.users.addNewUser(userId); //retorna el blackjack
        this.sendInit(100);
    }

    private void solveWinner() throws IOException {

        this.sendWINS();
    }

    public String handlerError() throws IOException {
        String err = this.comutils.readErrorMessage();
        this.writeLog("CLIENTE: " + err);
        return err ;
    }

    public void sendIDCK() throws IOException {
        this.stringToSend = "IDCK";
        this.comutils.writeCommand(this.stringToSend);
        for(Card card : this.blackJack.getPlayerHand().getHandCards()){
            this.comutils.write_SP();
            this.comutils.writeChar(card.getRank());
            this.comutils.writeChar(card.getCardProtcolNaipe().charAt(0));
            System.out.println(card.toString());
            this.writeLog("SERVIDOR: " + stringToSend + " " + card.toString());
        }
    }

    public void sendCARD() throws IOException {
        this.stringToSend = "CARD";
        this.comutils.writeCommand(this.stringToSend);
        this.comutils.write_SP();
        Card card = this.blackJack.dealPlayerCard();
        this.comutils.writeChar(card.getRank());
        this.comutils.writeChar(card.getCardProtcolNaipe().charAt(0));
        System.out.println(card.toString());
        this.writeLog("SERVIDOR: " + stringToSend + " " + card.toString());
    }

    public void sendSHOW() throws IOException {
        int lenght = this.blackJack.getDealerHand().getHandCards().size();
        this.stringToSend = "SHOW";
        this.comutils.writeCommand(this.stringToSend);
        this.comutils.write_SP();

        this.comutils.writeLen(lenght);

        for(Card card : this.blackJack.getDealerHand().getHandCards()) {
            this.comutils.write_SP();
            this.comutils.writeChar(card.getRank());
            this.comutils.writeChar(card.getCardProtcolNaipe().charAt(0));
            System.out.println(card.toString());
            this.writeLog("SERVIDOR: " + stringToSend + " " + card.toString());
        }
    }

    public void sendWINS() throws IOException {
        this.stringToSend = "WINS";
        this.comutils.writeCommand(this.stringToSend);
        this.comutils.write_SP();
        this.comutils.writeChar(this.blackJack.getWinner());
        this.comutils.write_SP();
        this.comutils.write_int32(this.blackJack.getPlayerBet()); //get the amount of chips earned
        this.writeLog("SERVIDOR: "+ stringToSend + " " + this.blackJack.getWinner() + " " + this.blackJack.getPlayerBet());
    }

    private void startNewGame() throws IOException {
        this.blackJack.restart();
        this.sendInit(100);
        this.sendIDCK();
        this.sendOneCard();
    }

    private void surrender() throws IOException {
        this.message = "WINS";
        this.comutils.writeCommand(this.message);
        this.comutils.write_SP();
        this.comutils.writeChar('2');
        this.comutils.write_SP();
        this.comutils.write_int32(100);
        this.writeLog("SERVIDOR: " + message);
    }

    private void doubleBet() throws Exception {
        if (this.blackJack.getPlayerBet() > this.blackJack.getPlayerHand().getCash()) {
            this.stringToSend = "ERRO";
            this.comutils.writeCommand(this.stringToSend);
            this.stringToSend = "Not enough money to double the bet";
            this.comutils.writeErrorMessage(this.stringToSend);
            this.writeLog("SERVIDOR: ERRO "+ stringToSend);
        }
        this.blackJack.doubleBet();
    }

    private void showCards() throws IOException {
        int length;
        String rank;
        String suit;
        this.blackJack.getPlayerHand().setHandCards(new ArrayList<>());
        this.comutils.read_Char();
        length = this.comutils.readLen();

        for (int i = 0; i < length; i++) {
            this.comutils.read_Char();
            rank = this.comutils.read_Char();
            if (rank.equals(String.valueOf('3')))
                rank = "H";
            else if (rank.equals(String.valueOf('4')))
                rank = "D";
            else if (rank.equals(String.valueOf('5')))
                rank = "C";
            else
                rank = "S";
            suit = this.comutils.read_Char();
            this.blackJack.getPlayerHand().take(rank.charAt(0), suit.charAt(0));
            this.writeLog("CLIENTE: "+ rank + suit);
        }
        this.sendSHOW();
    }

    private void askExtraCard() throws IOException {
        this.sendCARD();
    }

    private void setCash() throws IOException {
        int cash;
        this.comutils.read_Char();
        cash = this.comutils.read_int32();
        System.out.println(cash);
        if (cash < this.blackJack.MAX_BET){
            this.message = "ERRO";
            this.comutils.writeCommand(this.message);
            this.comutils.writeErrorMessage("The cash should be greater than 100");
            this.writeLog("SERVIDOR: "+ message + " " + " The cash should be greater than 100");
        } else {
            this.blackJack.setPlayerMoney(cash);
            this.sendIDCK();
            this.sendOneCard();
        }
    }

    private void sendOneCard() throws IOException {
        this.message = "SHOW";
        this.comutils.writeCommand(this.message);
        this.comutils.write_SP();
        this.comutils.writeLen(1);
        this.comutils.write_SP();
        Card card = this.blackJack.getDealerHand().getHandCards().get(0);
        this.comutils.writeCard(card.getRank(), card.getCardNaipe());
        this.writeLog("SERVIDOR: "+ message + " " + card.toString());
    }

    public void sendInit(int chips) throws IOException {
        String message = "INIT";
        this.comutils.writeCommand(message);
        this.comutils.write_SP();
        this.comutils.write_int32(chips);
        this.writeLog("SERVIDOR: "+ message + " " + chips);
    }

    public void writeLog(String mensaje) {

        Logger logger = Logger.getLogger("MyLog");
        String fileRute = "log/Server" + Thread.currentThread().getName() + ".log";

        try {

            file = new FileHandler(fileRute, true);
            logger.addHandler(file);
            logger.setUseParentHandlers(false);

            SimpleFormatter formatter = new SimpleFormatter();
            file.setFormatter(formatter);

            logger.info(mensaje);
            file.close();

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
