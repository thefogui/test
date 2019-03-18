package Controller;

import java.io.*;
import java.net.*;
import lib.*;

import java.util.Scanner;

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
        this.readSocket();
    }

    public void readSocket() throws IOException {
        while(this.blackJack.getIsRunning()) {
            this.message = this.comUtils.readCommand(); //read socket

            this.message = this.message.toUpperCase();

            System.out.println(this.message);
            switch (this.message) {
                case "INIT":
                    this.startTheGame();
                    break;
                case "IDCK":
                    this.takeaCard();
                    break;
                case "CARD":
                    this.takeAskedCard();
                    break;
                case "SHOW":
                    break;
                case "WINS":
                    this.decideTheWinner();
                    break;
                default:
                    System.err.println("It ins't a valid command. closing the game");
                    //set running to false

                    break;
            }
        }
    }

    private void decideTheWinner() {

    }

    private void takeAskedCard() throws IOException {
        String space = this.comUtils.read_Char();
        String rank = this.comUtils.read_Char();
        if (rank.equals(String.valueOf('3')))
            rank = "H";
        else if (rank.equals(String.valueOf('4')))
            rank = "D";
        else if (rank.equals(String.valueOf('5')))
            rank = "C";
        else
            rank = "S";
        String suit = this.comUtils.read_Char();
        Card card = new Card(rank.charAt(0), suit.charAt(0));
        this.blackJack.getPlayerHand().take(card);
        System.out.println("You got this card " + card.toString());
        System.out.println("Your amount is " + this.blackJack.getPlayerHand().getActualValue());
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
            this.comUtils.writeCard(card.getRank(), card.getCardNaipe());
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

    private void startTheGame() throws IOException{
        String space = this.comUtils.read_Char();
        this.message = "CASH";
        this.action();

    }

    private void action() {
        Scanner sc = new Scanner (System.in);
        switch (this.message) {
            case "CASH":
                this.actionCash(sc);
                break;
            case "HITT":

                break;
            case "SHOW":

                break;
            case "BETT":

                break;
            case "SRND":

                break;
            case "RPLY":

            case "EXIT":

            case "ERRO":
                try {
                    System.err.println(this.handlerError());
                } catch (IOException ex) {
                    System.err.println("Unable to read error message " + ex.getMessage());
                }
                break;
            default:

                break;
        }
    }

    public String handlerError() throws IOException {
        return this.comUtils.readErrorMessage();
    }

    private void actionCash(Scanner sc){
        System.out.println("Entry the amount of cash you want to use, remember the bet is 100 chips");
        int userInput = sc.nextInt(); //Invocamos un método sobre un objeto Scanner
        while(userInput < 100){
            System.err.println("Need to be greater than 100!");
            userInput = sc.nextInt();
        }

        try {
            this.sendCash(userInput);
        } catch (IOException ex) {
            System.err.println("Unable to send cash " + ex.getMessage());
        }
    }

    private void takeaCard() throws IOException {
        String naipe, rank;
        for (int i = 0; i < 2; i++){
            String space = this.comUtils.read_Char();
            naipe = this.comUtils.read_Char();
            rank = this.comUtils.read_Char();
            Card card = new Card(naipe.charAt(0), rank.charAt(0));
            //switch to get the suit/naipe
            this.blackJack.getPlayerHand().take(card);
            System.out.println("You got this card " + card.toString());
        }
        System.out.println("Your amount is " + this.blackJack.getPlayerHand().getActualValue());

        System.out.println("--------------------------------");
        System.out.println("Select a number:");
        System.out.println("1. Ask for a new card");
        System.out.println("2. Double the bet");
        System.out.println("--------------------------------");

        Scanner sc = new Scanner(System.in);
        int opcio = sc.nextInt();

        if (opcio == 1)
            this.sendHitt();
        else
            this.sendBet();
    }

}

