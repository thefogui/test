/**
 * Class that uses a simple automatic player
 *
 * @Authors Vitor Carvalho and Ivet Aymerich
 */

package View;

import Controller.Protocol;
import java.io.IOException;
import java.util.Random;

public class BlackJackSmart {
    private Protocol protocol;
    private String server;
    private int numPort;
    private int option;
    private int currentMoney;
    private String message;
    private boolean firstShow;
    private boolean firstCash;
    private boolean playerCanBet;
    private Random random;
    private int minBet;

    public BlackJackSmart(String server, int numPort) throws IOException {
        this.option = 0;
        this.server = server;
        this.numPort = numPort;
        this.message = "";
        this.firstShow = true;
        this.firstCash = true;
        this.playerCanBet = true;
        this.minBet = 0;
        this.random = new Random();
        this.connect();
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getNumPort() {
        return numPort;
    }

    public void setNumPort(int numPort) {
        this.numPort = numPort;
    }

    public int getOption() {
        return option;
    }

    public void setOption(int option) {
        this.option = option;
    }

    public int getCurrentMoney() {
        return currentMoney;
    }

    public void setCurrentMoney(int currentMoney) {
        this.currentMoney = currentMoney;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isFirstShow() {
        return firstShow;
    }

    public void setFirstShow(boolean firstShow) {
        this.firstShow = firstShow;
    }

    public boolean isFirstCash() {
        return firstCash;
    }

    public void setFirstCash(boolean firstCash) {
        this.firstCash = firstCash;
    }

    public boolean isPlayerCanBet() {
        return playerCanBet;
    }

    public void setPlayerCanBet(boolean playerCanBet) {
        this.playerCanBet = playerCanBet;
    }

    /**
     * Function that return a positive integer randomly
     * @return integer positive
     */
    public int anyRandomInt() {
        int random = this.random.nextInt();
        if (random < 0)
            return -random;
        return random;
    }

    /**
     * Function that returns a positive integer in range
     * @param low the minimal value
     * @param high the maximum value
     * @return integer positive
     */
    public int anyRandomIntRange(int low, int high) {
        return this.random.nextInt(high) + low;
    }

    /**
     * This function connects to the server using the Protocol class
     * @throws IOException an error can be occurred creating the socket.
     */
    private void connect() throws IOException {
        int username = this.anyRandomInt();
        System.out.println("--------------------------------");
        System.out.println("    Connecting to host...");
        System.out.println("    Username: " + username);
        this.protocol = new Protocol(server, numPort, username);
        System.out.println("--------------------------------");
    }

    /**
     * this function start the game sending the initial message using the protool
     *
     * @throws IOException an error sending the message using the socket
     */
    public void start() throws IOException {
        this.protocol.start();
        this.readSocket();
    }

    /**
     * It send the player chips generated randomly to the server
     * @throws IOException error writing in the socket
     */
    public void sendCash() throws IOException {
        int cash;

        if (this.protocol.getPlayerCash() == 0)
            cash = this.anyRandomIntRange(this.minBet, Integer.MAX_VALUE);
        else
            cash = this.protocol.getPlayerCash();

        System.out.println("--------------------------------");
        System.out.println("    Sending cash: " + cash);
        System.out.println("--------------------------------");
        this.protocol.sendCash(cash);
    }

    /**
     * While the game is running it reads the socket and call others functions based on the message read
     * @throws IOException error reading the socket.
     */
    private void readSocket() throws IOException {
        while(this.protocol.getIsRunning()) {
            try {
                this.message = this.protocol.readCommand(); //read socket
                this.message = this.message.toUpperCase();

                System.out.println(this.message);
                switch (this.message) {
                    case "INIT":
                        this.readMinBet();
                        this.sendCash();
                        this.playerCanBet = true;
                        this.firstShow = true;
                        break;
                    case "IDCK":
                        this.takeTheInitialCards();
                        break;
                    case "CARD":
                        this.takeAcard();
                        this.getAction();
                        break;
                    case "SHOW":
                        if (this.firstShow) {
                            this.printCard();
                            this.firstShow = false;
                            this.getAction();
                        } else {
                            this.printDealerCards();
                        }
                        break;
                    case "WINS":
                        this.checkWinner();
                        this.replayOrExit();
                        break;
                    case "ERRO":
                        this.protocol.handlerError();
                        //enviar exit
                        this.protocol.setPlaying(false);
                        break;
                    default:
                        System.err.println("It ins't a valid command. closing the game");
                        this.protocol.setPlaying(false);
                        break;
                }

            } catch (IOException ex) {
                System.err.println("Error reading the Socket: " + ex.getMessage());
            }
        }
    }

    /**
     * Read the min bet sent by the server and print it
     * @throws IOException error reading the socket.
     */
    private void readMinBet() throws IOException {
        String sp = this.protocol.read_sp();
        this.minBet = this.protocol.readInteger();
        System.out.println("--------------------------------");
        System.out.println("    Min bet: " + this.minBet);
        System.out.println("--------------------------------");
    }

    /**
     * This function selects between play another game or exit, the probability of exit is 5%
     * @throws IOException error writing in the socket
     */
    private void replayOrExit() throws IOException {
        int action = this.anyRandomIntRange(0,100);
        if (action > 5) {
            this.protocol.sendReplay();
            this.protocol.reset();
            System.out.println("--------------------------------");
            System.out.println("    Starting new game");
            System.out.println("--------------------------------");
        } else {
            this.exitAndClose();
            this.protocol.setPlaying(false);
        }
    }

    /**
     * Close the game sending an 'EXIT' message to the server
     * @throws IOException error writing in the socket
     */
    private void exitAndClose() throws IOException {
        this.protocol.sendExit();
        System.out.println("--------------------------------");
        System.out.println("    Closing the game!");
        System.out.println("--------------------------------");
    }

    /**
     * This function take the first 2 initial cards from the server
     * and print the player current score
     * @throws IOException error reading the socket
     */
    private void takeTheInitialCards() throws IOException {
        System.out.println("--------------------------------");
        for (int i = 0; i < 2; i++){
            System.out.println("    Card " +  this.protocol.takeACard());
        }
        System.out.println("    Your amount is " + this.protocol.handAmount());
        System.out.println("--------------------------------");
    }

    /**
     * Takes an extra card sent by the server by reading the socket.
     * Pints the actual player score
     * @throws IOException error reading the socket
     */
    private void takeAcard() throws IOException {
        System.out.println("--------------------------------");
        System.out.println("    Card " +  this.protocol.takeACard());
        System.out.println("    Your amount is " + this.protocol.handAmount());
        System.out.println("--------------------------------");
    }

    private void printCard() throws IOException {
        System.out.println("--------------------------------");
        System.out.println("    Dealer initial card " + this.protocol.takeDealerCard());
        System.out.println("--------------------------------");
    }

    /**
     * This function uses selects randomly and based on the player points what to do
     * @throws IOException error reading and writing in the socket
     */
    private void getAction() throws IOException {
        int action = 0;
        if (this.playerCanBet) {
            if (this.protocol.getplayerScore() < 17) {
                action = this.anyRandomIntRange(0,2);
                if (action == 0)
                    this.protocol.sendHitt();
                else {
                    this.protocol.sendBet();
                    this.protocol.sendHitt();
                }
            } else if (this.protocol.getplayerScore() == 21) {
                this.protocol.sendShow();
                this.protocol.reset();
            } else if (this.protocol.getplayerScore() > 21) {
                this.protocol.sendShow();
            } else {
                action = this.anyRandomIntRange(0,100);
                if (action < 10) {
                    this.protocol.sendSurrender();
                    this.protocol.reset();
                } else {
                    this.protocol.sendShow();
                    this.protocol.reset();
                }
            }
        }
    }

    /**
     * It prints the server cards sent by the show, not the initial one
     * @throws IOException error reading the socket
     */
    private void printDealerCards() throws IOException {
        System.out.println("--------------------------------");
        System.out.println("    Server cards: " + this.protocol.getServerCards());
        System.out.println("--------------------------------");
    }

    /**
     * This function reads the socket and print the winner,
     * also sum the amount of chips lost or worn.
     * @throws IOException error reading the socket
     */
    private void checkWinner() throws IOException {
        String winner = this.protocol.checkWinner();
        System.out.println("--------------------------------");
        System.out.println(winner);
        System.out.println("--------------------------------");
    }
}
