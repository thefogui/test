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

    public int anyRandomInt() {
        return this.random.nextInt();
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
    }

    public void sendCash() throws IOException {
        int cash = this.anyRandomIntRange(this.minBet, Integer.MAX_VALUE);
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
                        break;
                    case "IDCK":
                        this.takeTheInitialCards();
                        break;
                    case "CARD":
                        this.takeAcard();
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
                        this.firstShow = true;
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

    }

    private void takeTheInitialCards() throws IOException {

    }

    private void takeAcard() throws IOException {

    }

    private void printCard() throws IOException {

    }

    private void getAction() throws IOException {

    }

    private void printDealerCards() throws IOException {

    }

    private void checkWinner() throws IOException {

    }
}
