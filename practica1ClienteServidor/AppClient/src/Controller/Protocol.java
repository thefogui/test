/*
 * Class protocol, it has the functions to communicate with the server.
 *
 * Authors: Vitor Carvalho and Ivet Aymerich
 */

package Controller;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import lib.*;

/**
 *
 */
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

    /**
     * Retorna the BalckJack class
     * @return object of the BlackJack Class
     */
    public BlackJack getBlackJack() {
        return blackJack;
    }

    public void setBlackJack(BlackJack blackJack) {
        this.blackJack = blackJack;
    }

    /**
     * Start the game sending a 'STRT' to the server
     * @throws IOException error writing the message in the socket
     */
    public void start() throws IOException {
        //Start the game here and connect to server
        this.message = "STRT";
        this.comUtils.write_string(this.message);
        this.comUtils.write_SP();
        this.comUtils.write_int32(this.blackJack.getPlayerID());
    }

    /**
     * Send the 'CASH' message to the server with the amount of chips
     * @param chips the amount of chips the user has
     * @throws IOException error writing the message in the socket
     */
    public void sendCash(int chips) throws IOException {
        this.message = "CASH";
        this.comUtils.write_string(this.message);
        this.comUtils.write_SP();
        this.comUtils.write_int32(chips);
        this.blackJack.setPlayerMoney(chips);
    }

    /**
     * Send the hit message to the server it expects a 'CARD' as message from the server
     * @throws IOException error writing the message in the socket
     */
    public void sendHitt() throws IOException {
        this.message = "HITT";
        this.comUtils.write_string(this.message);
    }

    /**
     * It sends the cards in the hand to the server.
     * @throws IOException error writing the message in the socket
     */
    public void sendShow() throws IOException {
        int length = this.blackJack.getPlayerHand().getHandCards().size();
        this.message = "SHOW";
        this.comUtils.write_string(this.message);

        this.comUtils.write_SP();
        this.comUtils.writeChar(Character.forDigit(length,10));

        for(Card card : this.blackJack.getPlayerHand().getHandCards()) {
            this.comUtils.write_SP();
            this.comUtils.writeChar(card.getRank());
            this.comUtils.writeChar(card.getCardNaipe());
        }
    }

    /**
     * Send a bet to the server to double the bet, usually the 'HITT' message is sent too
     * @throws IOException error writing the message in the socket
     */
    public void sendBet() throws IOException {
        this.message = "BETT";
        this.comUtils.write_string(this.message);
    }

    /**
     * It sends the surrender message to the server
     * @throws IOException error writing the message in the socket
     */
    public void sendSurrender() throws IOException {
        this.message = "SRND";
        this.comUtils.write_string(this.message);
    }

    /**
     * This function sends a replay message to the server to start a new game, a 'INIT'
     * message is expected from the server.
     * @throws IOException error writing the message in the socket
     */
    public void sendReplay() throws IOException {
        this.message = "RPLY";
        this.comUtils.write_string(this.message);
    }

    /**
     * This function reads the socket for the error message
     * @return the error message
     * @throws IOException error reading the message in the socket
     */
    public String handlerError() throws IOException {
        String space = this.comUtils.read_Char();
        return this.comUtils.readErrorMessage();
    }

    /**
     * This function reads a card from the server and adds to the user hand
     * @return return the card tostring
     * @throws IOException error reading the message in the socket
     */
    public String takeACard() throws IOException {
        String space = this.read_sp();
        String naipe, rank;
        rank = this.comUtils.read_Char();
        naipe = this.comUtils.read_Char();;
        Card card = new Card(naipe.charAt(0), rank.charAt(0));
        this.blackJack.getPlayerHand().take(card);
        return card.toString();
    }

    /**
     * This function gets the initial dealer card
     * @return returns the string of the card
     * @throws IOException error reading the message in the socket
     */
    public String takeDealerCard() throws IOException {
        String space = this.read_sp();
        int length = Integer.parseInt(String.valueOf(this.comUtils.read_Char()));
        space = this.read_sp();
        String naipe, rank;
        rank = this.comUtils.read_Char();
        naipe = this.comUtils.read_Char();
        Card card = new Card(naipe.charAt(0), rank.charAt(0));
        this.blackJack.getDealerHand().take(card);
        return card.toString();
    }

    /**
     * This functions allows the client reads the socket for the server command
     * @return return the message
     * @throws IOException error reading the message in the socket
     */
    public String readCommand() throws IOException {
        return this.comUtils.readCommand();
    }

    /**
     * This function reads the ' '
     * @return retorna the char
     * @throws IOException error reading the message in the socket
     */
    public String read_sp() throws IOException {
        return this.comUtils.read_Char();
    }

    /**
     * return the boolean that is used to check if the game is running or not
     * @return
     */
    public boolean getIsRunning() {
        return this.blackJack.getIsRunning();
    }

    /**
     * Return tne value of the player cash
     * @return a String value of the player cash
     */
    public String handAmount() {
        return String.valueOf(this.blackJack.getPlayerHand().getActualValue());
    }

    /**
     * This functions read an integer from the socket
     * @return return the integer value
     * @throws IOException error reading the message in the socket
     */
    public int readInteger() throws IOException {
        return this.comUtils.read_int32();
    }

    /**
     * This fucntion checks the winner of the game and write it in the socket
     *
     * @return a string value that informs the winner '0' the player is the winner,
     * '1' the dealer is the winner, '2' the
     * game is tie up
     * @throws IOException error reading the message in the socket
     */
    public String checkWinner() throws IOException{
        String space = this.read_sp();
        String winner = this.comUtils.read_Char();
        space = this.read_sp();
        int amount = this.comUtils.read_int32();

        switch (winner) {
            case "0":
                this.blackJack.setPlayerMoney(this.blackJack.getPlayerMoney() + amount);
                return "You are the winner, cash earned: " + amount;
            case "1":
                this.blackJack.setPlayerMoney(this.blackJack.getPlayerMoney() - amount);
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

    /**
     * Get the dealer hand
     *
     * @return return the cards of the dealer hand in a tring format separates by a space
     * @throws IOException error reading the message in the socket
     */
    public String getServerCards() throws IOException {
        String cards = "";
        String space = this.read_sp();
        int length = Integer.parseInt(String.valueOf(this.comUtils.read_Char()));
        String naipe, rank;

        for (int i = 0; i < length; i++) {
            space = this.read_sp();
            rank = this.comUtils.read_Char();
            naipe = this.comUtils.read_Char();
            Card card = new Card(naipe.charAt(0), rank.charAt(0));
            this.blackJack.getDealerHand().take(card);
            cards += card.toString() + " ";
        }
        return cards;
    }

    /**
     * This function sends an error message to the server.
     * @param error the error message the clients wants to send to the server
     * @throws IOException error writing the message in the socket
     */
    private void sendError(String error) throws IOException {
        int length = error.length();
        char [] chars = ("" + length).toCharArray();
        this.comUtils.writeErrorMessage(chars[0], chars[1], length, error);
    }

    /**
     * This function send an EXIT message to the server.
     * @throws IOException error writing the message in the socket
     */
    public void sendExit() throws IOException {
        this.message = "EXIT";
        this.comUtils.writeCommand(this.message);
    }

    public void setPlaying(boolean b) {
        this.blackJack.setRunning(b);
    }

    public int getPlayerCash() {
        return this.blackJack.getPlayerMoney();
    }

    /**
     * This function return the player cash
     * @return int that refers to the player actual cash
     */
    public int getplayerScore() {
        return this.blackJack.getPlayerHand().getActualValue();
    }

    /**
     * This function cleans the user and the dealer hands
     * and reset the amount of cash of both players
     */
    public void reset() {
        this.blackJack.getDealerHand().getHandCards().clear();
        this.blackJack.getPlayerHand().getHandCards().clear();
        this.blackJack.getDealerHand().setActualValue(0);
        this.blackJack.getPlayerHand().setActualValue(0);
    }

    /**
     * This function return the dealer hand cards
     * @return an ArrayList that contains the dealer hand
     */
    public ArrayList<Card> getDealerHand() {
        return this.blackJack.getDealerHand().getHandCards();
    }
}

