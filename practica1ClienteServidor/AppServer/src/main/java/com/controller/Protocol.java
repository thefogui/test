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

/**
 * Protocol class, this class allows the communication between clients and the server
 *
 * @author Vitor Carvalho and Ivet Aymerich
 */
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

    /**
     * Constructor, create the game and start the protocol attributes
     * @param socket the socket that is assigned to this actual client
     * @param users list of users
     * @throws IOException error starting the socket
     */
    public Protocol(Socket socket, Users users) throws IOException {
        this.command = null;
        this.stringToSend = null;
        this.users = users;
        this.blackJack = new BlackJack(1);
        this.firstCash = true;
        this.socket = socket;
        this.comutils = new ComUtils(this.socket);
    }

    /**
     * This function sis the core of the class, it reads the socket and based on client messages do a thing or another
     * @throws IOException error reading the socket.
     */
    public void readSocket() throws IOException {
        while (this.blackJack.getIsRunning())  {
            try {

                this.command = this.comutils.readCommand();
                this.command = this.command.toUpperCase();

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
                        if (this.blackJack.getPlayerHand().getActualValue() >= 21) {
                            this.sendClientLost();
                        } else {
                            this.showCards();

                            this.solveWinner();
                        }
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

    /**
     * In case player exceed 21 points in this hands dealer don’t SHOW his hand and send directly a WIN ‘1’ command (dealer wins).
     * @throws IOException error writing in the scoket
     */
    private void sendClientLost() throws IOException {
        this.stringToSend = "WINS";
        this.comutils.writeCommand(this.stringToSend);
        this.comutils.write_SP();
        this.comutils.writeChar('1');
        this.comutils.write_SP();
        this.comutils.write_int32(this.blackJack.getPlayerBet()); //get the amount of chips earned
        this.writeLog("SERVIDOR: "+ stringToSend + " 1 " + this.blackJack.getPlayerBet());
    }

    /**
     * This function reads the socket and gets the user ID and send a 'INIT' message to him
     * @throws IOException error writing and reading the socket
     */
    private void startAGame() throws IOException {
        String space = this.comutils.read_Char();
        int userId = this.comutils.read_int32();
        this.writeLog("CLIENTE: " + userId);
        this.users.addNewUser(userId); //retorna el blackjack
        this.sendInit(100);
    }

    /**
     * Function that solves the winner and send it to the client
     * @throws IOException error writing in the socket
     */
    private void solveWinner() throws IOException {

        this.sendWINS();
    }

    /**
     * This function reads the client error and return its.
     * @return String that contains the error message
     * @throws IOException error reading the socket
     */
    private String handlerError() throws IOException {
        String err = this.comutils.readErrorMessage();
        this.writeLog("CLIENTE: " + err);
        return err ;
    }

    /***
     * Sens the 'IDCK' message to the server with the player initial cards
     * @throws IOException error writing in the socket
     */
    private void sendIDCK() throws IOException {
        this.stringToSend = "IDCK";
        this.comutils.writeCommand(this.stringToSend);
        for(Card card : this.blackJack.getPlayerHand().getHandCards()) {
            this.comutils.write_SP();
            this.comutils.writeChar(card.getRank());
            this.comutils.writeChar(card.getCardProtcolNaipe());
            this.writeLog("SERVIDOR: " + stringToSend + " " + card.toString());
        }
    }

    /**
     * Send the extra card asked by the user.
     * @throws IOException error writing in the socket.
     */
    private void sendCARD() throws IOException {
        this.stringToSend = "CARD";
        this.comutils.writeCommand(this.stringToSend);
        this.comutils.write_SP();
        Card card = this.blackJack.dealPlayerCard();
        this.comutils.writeChar(card.getRank());
        this.comutils.writeChar(card.getCardProtcolNaipe());
        this.writeLog("SERVIDOR: " + stringToSend + " " + card.toString());
    }

    /**
     * Send the show message to the client with the dealer cards
     * @throws IOException error reading and writing in the socket
     */
    private void sendSHOW() throws IOException {
        int length = this.blackJack.getDealerHand().getHandCards().size();
        this.stringToSend = "SHOW";
        this.comutils.writeCommand(this.stringToSend);
        this.comutils.write_SP();

        this.comutils.writeChar(Character.forDigit(length,10));

        for(Card card : this.blackJack.getDealerHand().getHandCards()) {
            this.comutils.write_SP();
            this.comutils.writeChar(card.getRank());
            this.comutils.writeChar(card.getCardProtcolNaipe());
            this.writeLog("SERVIDOR: " + stringToSend + " " + card.toString());
        }
    }

    /**
     * it solves the winner and send it to the client.
     * @throws IOException error writing in the socket
     */
    private void sendWINS() throws IOException {
        this.stringToSend = "WINS";
        this.comutils.writeCommand(this.stringToSend);
        this.comutils.write_SP();
        this.comutils.writeChar(this.blackJack.getWinner());
        this.comutils.write_SP();
        this.comutils.write_int32(this.blackJack.getPlayerBet()); //get the amount of chips earned
        this.writeLog("SERVIDOR: "+ stringToSend + " " + this.blackJack.getWinner() + " " + this.blackJack.getPlayerBet());
    }

    /**
     * Start a game and sends the IDCK And the first SHOW message
     * @throws IOException error writing in the socket
     */
    private void startNewGame() throws IOException {
        this.blackJack.restart();
        this.sendInit(100);
        this.sendIDCK();
        this.sendOneCard();
    }

    /**
     * IF the player surrender this function sends him a tie message
     * @throws IOException error writing in the socket
     */
    private void surrender() throws IOException {
        this.message = "WINS";
        this.comutils.writeCommand(this.message);
        this.comutils.write_SP();
        this.comutils.writeChar('2');
        this.comutils.write_SP();
        this.comutils.write_int32(this.blackJack.getPlayerBet());
        this.writeLog("SERVIDOR: " + message);
    }

    /**
     * This function doubles the actual bet and it is an answer to the BETT message sent by the client
     *
     * @throws Exception error writing and reading the socket.
     */
    private void doubleBet() throws Exception {
        if (this.blackJack.getPlayerBet() > this.blackJack.getPlayerHand().getCash()) {
            this.stringToSend = "ERRO";
            this.comutils.writeCommand(this.stringToSend);
            this.stringToSend = "Not enough money to double the bet";
            this.sendErrorMessage(this.stringToSend);
            this.writeLog("SERVIDOR: ERRO "+ stringToSend);
        }
        this.blackJack.doubleBet();
    }

    /**
     * Function that gets the player cards and send the dealer cards
     * @throws IOException error reading and writing in the socket
     */
    private void showCards() throws IOException {
        int length;
        String rank;
        String suit;
        this.blackJack.getPlayerHand().setHandCards(new ArrayList<>());
        this.comutils.read_Char();

        length = Integer.parseInt(String.valueOf(this.comutils.read_Char()));

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
        this.blackJack.dealerAskCard();
        this.sendSHOW();
    }

    /**
     * if the player ask for a new card this we send him a new card with this function
     * @throws IOException error writing in the socket
     */
    private void askExtraCard() throws IOException {
        this.sendCARD();
    }

    /**
     * Read the cash message to the client and send the 'IDCK' AND 'SHOW' cards
     * @throws IOException error reading and writing in the socket.
     */
    private void setCash() throws IOException {
        int cash;
        this.comutils.read_Char();
        cash = this.comutils.read_int32();
        if (cash < this.blackJack.MIN_BET){
            this.message = "ERRO";
            this.comutils.writeCommand(this.message);

            this.sendErrorMessage("The cash should be greater than 100");
            this.writeLog("SERVIDOR: "+ message + " " + " The cash should be greater than 100");
        } else {
            this.blackJack.setPlayerMoney(cash);
            this.sendIDCK();
            this.sendOneCard();
        }
    }

    /**
     * Sends the first dealer card to the client
     * @throws IOException error writing in the socket
     */
    private void sendOneCard() throws IOException {
        this.message = "SHOW";
        this.comutils.writeCommand(this.message);
        this.comutils.write_SP();
        this.comutils.writeChar('1');
        this.comutils.write_SP();
        Card card = this.blackJack.getDealerHand().getHandCards().get(0);
        this.comutils.writeChar(card.getRank());
        this.comutils.writeChar(card.getCardNaipe());
        this.writeLog("SERVIDOR: "+ message + " " + card.toString());
    }

    /**
     * Send the 'INIT' to the client
     * @param chips the minimal bet
     * @throws IOException error writing in the socket.
     */
    private void sendInit(int chips) throws IOException {
        String message = "INIT";
        this.comutils.writeCommand(message);
        this.comutils.write_SP();
        this.comutils.write_int32(chips);
        this.writeLog("SERVIDOR: "+ message + " " + chips);
    }

    /**
     * Function that creates a log file for eah client and write a message
     * @param mensaje message to write in the log
     * @throws SecurityException exceptiong with the file
     * @throws IOException error creating/reading/writing in the file
     */
    private void writeLog(String mensaje) throws SecurityException, IOException {
        Logger logger = Logger.getLogger("MyLog");
        String fileRute = "log/ServerClient" + this.blackJack.getPlayerID() + ".log";

        file = new FileHandler(fileRute, true);
        logger.addHandler(file);
        logger.setUseParentHandlers(false);

        SimpleFormatter formatter = new SimpleFormatter();
        file.setFormatter(formatter);

        logger.info(mensaje);
        file.close();
    }

    /**
     * Wites an error message in the socket
     * @param message String
     * @throws IOException error writing in the socket.
     */
    private void sendErrorMessage(String message) throws IOException {
        int length = message.length();
        char [] chars = ("" + length).toCharArray();
        this.comutils.writeErrorMessage(chars[0], chars[1], length, message);
    }
}
