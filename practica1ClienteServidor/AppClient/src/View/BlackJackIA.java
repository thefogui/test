/*
 * This class generates an automatic player fo the game using the blackjack
 * AI algorithm.
 *
 * Authors: Vitor Carvalho and Ivet Aymerich
 */

package View;

import Controller.Protocol;
import java.io.IOException;
import java.util.Random;
import lib.Card;

public class BlackJackIA {

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

    public BlackJackIA(String server, int numPort) throws IOException {
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

    public int anyRandomInt() {
        int random = this.random.nextInt();
        if (random < 0)
            return -random;
        return random;
    }

    public int anyRandomIntRange(int low, int high) {
        return this.random.nextInt(high) + low;
    }

    private void connect() throws IOException {
        int username = this.anyRandomInt();
        System.out.println("--------------------------------");
        System.out.println("    Connecting to host...");
        System.out.println("    Username: " + username);
        this.protocol = new Protocol(server, numPort, username);
        System.out.println("--------------------------------");
    }

    public void start() throws IOException {
        this.protocol.start();
        this.readSocket();
    }

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

    private void readMinBet() throws IOException {
        String sp = this.protocol.read_sp();
        this.minBet = this.protocol.readInteger();
        System.out.println("--------------------------------");
        System.out.println("    Min bet: " + this.minBet);
        System.out.println("--------------------------------");
    }

    private void replayOrExit() throws IOException {
        int action = this.anyRandomIntRange(0,100);
        if (action > 30) {
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

    private void exitAndClose() throws IOException {
        this.protocol.sendExit();
        System.out.println("--------------------------------");
        System.out.println("    Closing the game!");
        System.out.println("--------------------------------");
    }

    private void takeTheInitialCards() throws IOException {
        System.out.println("--------------------------------");
        for (int i = 0; i < 2; i++){
            System.out.println("    Card " +  this.protocol.takeACard());
        }
        System.out.println("    Your amount is " + this.protocol.handAmount());
        System.out.println("--------------------------------");
    }

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

    private void getAction() throws IOException {
        if (this.protocol.getPlayerCash() >= 17 && this.protocol.getPlayerCash() <= 20) {
            //show
        } else if (this.protocol.getPlayerCash() == 16){
            for (Card card : this.protocol.getDealerHand()){
                if (card.getRank() == '7' || card.getRank() == '8') {
                    this.protocol.sendHitt();
                } else if (card.getRank() == '9' || card.getRank() == 'X' || card.getRank() == 'A') {
                    this.protocol.sendSurrender();
                    this.protocol.sendReplay();
                } else {
                    this.protocol.sendShow();
                }
            }
        } else if (this.protocol.getPlayerCash() == 15) {
            for (Card card : this.protocol.getDealerHand()){
                if (card.getRank() == '7' || card.getRank() == '8' || card.getRank() == '9'
                        || card.getRank() == 'A') {
                    this.protocol.sendHitt();
                } else if (card.getRank() == 'X') {
                    this.protocol.sendSurrender();
                    this.protocol.sendReplay();
                } else {
                    this.protocol.sendShow();
                }
            }
        } else if (this.protocol.getPlayerCash() == 13 || this.protocol.getPlayerCash() == 14) {
            for (Card card : this.protocol.getDealerHand()){
                if (card.getRank() == '7' || card.getRank() == '8' || card.getRank() == '9'
                        || card.getRank() == 'A' || card.getRank() == 'X') {
                    this.protocol.sendHitt();
                } else {
                    this.protocol.sendShow();
                }
            }
        } else if (this.protocol.getPlayerCash() == 12) {
            for (Card card : this.protocol.getDealerHand()){
                if (card.getRank() == '2' || card.getRank() == '3' || card.getRank() == '7'
                        || card.getRank() == '8' || card.getRank() == '9'
                        || card.getRank() == 'A' || card.getRank() == 'X') {
                    this.protocol.sendHitt();
                } else {
                    this.protocol.sendShow();
                }
            }
        } else if (this.protocol.getPlayerCash() == 11) {
            this.protocol.sendBet();
            this.protocol.sendHitt();
        } else if (this.protocol.getPlayerCash() == 10) {
            for (Card card : this.protocol.getDealerHand()){
                if (card.getRank() == 'X' || card.getRank() == 'A') {
                    this.protocol.sendHitt();
                } else {
                    this.protocol.sendBet();
                    this.protocol.sendHitt();
                }
            }
        } else if (this.protocol.getPlayerCash() == 9) {
            for (Card card : this.protocol.getDealerHand()){
                if (card.getRank() == '3' || card.getRank() == '4' ||
                    card.getRank() == '5' || card.getRank() == '6') {
                    this.protocol.sendBet();
                    this.protocol.sendHitt();
                } else {
                    this.protocol.sendHitt();
                }
            }
        } else {
            this.protocol.sendHitt();
        }
    }

    private void printDealerCards() throws IOException {
        System.out.println("--------------------------------");
        System.out.println("    Server cards: " + this.protocol.getServerCards());
        System.out.println("--------------------------------");
    }

    private void checkWinner() throws IOException {
        String winner = this.protocol.checkWinner();
        System.out.println("--------------------------------");
        System.out.println(winner);
        System.out.println("--------------------------------");
    }
}
