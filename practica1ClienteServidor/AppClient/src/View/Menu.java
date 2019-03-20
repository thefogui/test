package View;

import Controller.Protocol;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {
    private Protocol protocol;
    private Scanner scanner;
    private String server;
    private int numPort;
    private int option;
    private String message;
    private boolean firstShow;
    private boolean firstCash;
    private boolean playerCanBet;
    private static final int CONNECT_TO_SERVER = 1;
    private static final int AUTOMATIC_CLIENT = 2;
    private static final int MANUAL_CLIENT = 3;
    private static final int EXIT = 4;

    public Menu(String server, int numPort) {
        this.scanner = new Scanner(System.in);
        this.option = 0;
        this.server = server;
        this.numPort = numPort;
        this.message = "";
        this.firstShow = true;
        this.firstCash = true;
        this.playerCanBet = true;
    }

    public void mainMenu() {
        while(option != EXIT) {
            System.out.println("--------------------------------");
            System.out.println("    1. Connect to server");
            System.out.println("    2. Automatic client");
            System.out.println("    3. Sub menu client");
            System.out.println("    4. Exit");
            System.out.println("    Choose an option");
            System.out.println("--------------------------------");

            try {
                option = scanner.nextInt();

                switch (option) {
                    case CONNECT_TO_SERVER:
                        this.connectToServer();
                    case AUTOMATIC_CLIENT:
                        System.out.println("Has seleccionado la opcion 2");
                        break;
                    case MANUAL_CLIENT:
                        System.out.println("Has seleccionado la opcion 3");
                        break;
                    case EXIT:
                        try {
                            this.finalize();
                        } catch (IOException ex) {
                            System.err.println("Error sending the EXIT command " + ex.getMessage());
                        } catch (Throwable throwable) {
                            System.err.println("Error sending the EXIT command " + throwable.getMessage());
                        }
                        break;
                    default:
                        System.err.println("Not a valid number!");
                }
            } catch (InputMismatchException e) {
                System.err.println("You need to introduce a number!");
                scanner.next();
            }
        }
    }

    private void connectToServer() {
        int username = 0;
        System.out.println("--------------------------------");
        System.out.println("    Connecting to host...");

        try {
            System.out.println("    Username:");
            System.out.println("--------------------------------");
            username = scanner.nextInt();
        } catch (Exception e) {
            System.err.println("Not a valid number!");
        }

        try {
            this.protocol = new Protocol(server, numPort, username);
            this.protocol.start();
            this.readSocket();
        }catch (IOException e) {
            System.err.println("Can't start the protocol " + e.getMessage());
        }
    }

    public void readSocket() {
        while(this.protocol.getIsRunning()) {
            try {
                this.message = this.protocol.readCommand(); //read socket
                this.message = this.message.toUpperCase();

                System.out.println(this.message);
                switch (this.message) {
                    case "INIT":
                        this.sendCash();
                        this.playerCanBet = true;
                        break;
                    case "IDCK":
                        //this.protocol.reset();
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
                        this.menuReplay();
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

    private void checkWinner() throws IOException {
        String winner = this.protocol.checkWinner();
        System.out.println("--------------------------------");
        System.out.println(winner);
        System.out.println("--------------------------------");
    }

    private void printDealerCards() throws IOException {
        System.out.println("--------------------------------");
        System.out.println("    Server cards: " + this.protocol.getServerCards());
        System.out.println("--------------------------------");
        //reset dealer hand
    }

    private void printCard() throws IOException {
        System.out.println("--------------------------------");
        System.out.println("    Dealer initial card " + this.protocol.takeDealerCard());
        System.out.println("--------------------------------");

    }

    private void takeAcard() throws IOException {
        System.out.println("--------------------------------");
        System.out.println("    Card " +  this.protocol.takeACard());
        System.out.println("    Your amount is " + this.protocol.handAmount());
        System.out.println("--------------------------------");
        this.getAction();
    }

    private void sendCash() throws IOException {
        String space = this.protocol.read_sp();
        int bet = this.protocol.readInteger();
        int cash;
        System.out.println("--------------------------------");
        System.out.println("    Actual bet: " + bet);

        if (this.firstCash) {
            System.out.println("    Entry the amount of cash:");
            System.out.println("--------------------------------");
            cash = this.scanner.nextInt();
            this.protocol.sendCash(cash);
            this.firstCash = false;
        } else {
            this.protocol.sendCash(this.protocol.getPlayerCash());
            System.out.println("--------------------------------");
        }
    }

    private void takeTheInitialCards() throws IOException {
        System.out.println("--------------------------------");
        for (int i = 0; i < 2; i++){
            System.out.println("    Card " +  this.protocol.takeACard());
        }
        System.out.println("    Your amount is " + this.protocol.handAmount());
        System.out.println("--------------------------------");
    }

    private void getAction() throws IOException {
        int opcio;
        if (this.playerCanBet) {
            this.playerCanBet = false;
            if (this.protocol.getplayerScore() < 21) {
                System.out.println("--------------------------------");
                System.out.println("    Select a number:");
                System.out.println("    1. Ask for a new card");
                System.out.println("    2. Double the bet");
                System.out.println("    3. Send cards");
                System.out.println("    4. Surrender");
                System.out.println("--------------------------------");
                opcio = this.scanner.nextInt();

                if (opcio == 1) {
                    this.protocol.sendHitt();

                }else if(opcio == 2){
                    this.protocol.sendBet();
                    this.getAction();
                } else if (opcio == 3) {
                    this.protocol.sendShow();
                    this.protocol.reset();
                }else if (opcio == 4) {
                    this.protocol.sendSurrender();
                    this.protocol.reset();
                }

            } else {
                System.out.println("--------------------------------");
                System.out.println("    Select a number:");
                System.out.println("    1. Double the bet");
                System.out.println("    2. Send cards");
                System.out.println("    3. Surrender");
                System.out.println("--------------------------------");
                opcio = this.scanner.nextInt();

                if (opcio == 1) {
                    this.protocol.sendBet();
                    this.getAction();
                }else if(opcio == 2){
                    this.protocol.sendShow();
                    this.protocol.reset();
                } else if (opcio == 3) {
                    this.protocol.sendSurrender();
                    this.protocol.reset();
                }
            }
        } else {
            if (this.protocol.getplayerScore() < 21) {
                System.out.println("--------------------------------");
                System.out.println("    Select a number:");
                System.out.println("    1. Ask for a new card");
                System.out.println("    2. Send cards");
                System.out.println("--------------------------------");
                opcio = this.scanner.nextInt();

                if (opcio == 1)
                    this.protocol.sendHitt();
                else if (opcio == 2) {
                    this.protocol.sendShow();
                    this.protocol.reset();
                }

            } else {
                System.out.println("--------------------------------");
                System.out.println("    Select a number:");
                System.out.println("    1. Send cards");
                System.out.println("--------------------------------");
                opcio = this.scanner.nextInt();

                if (opcio == 1) {
                    this.protocol.sendShow();
                    this.protocol.reset();
                }
            }
        }
    }

    private void exitAndClose() throws IOException {
        this.protocol.sendExit();
        System.out.println("--------------------------------");
        System.out.println("    Closing the game!");
        System.out.println("--------------------------------");
    }

    private void menuReplay() throws IOException {
        int opcio;
        System.out.println("--------------------------------");
        System.out.println("    Select a number:");
        System.out.println("    1. Play a new game");
        System.out.println("    2. Exit");
        System.out.println("    Actual cash: " + this.protocol.getPlayerCash());
        System.out.println("--------------------------------");
        opcio = this.scanner.nextInt();

        if (opcio == 1) {
            this.protocol.sendReplay();
            this.protocol.reset();
        }else if(opcio == 2){
            this.exitAndClose();
            this.protocol.setPlaying(false);
        }
    }
}
