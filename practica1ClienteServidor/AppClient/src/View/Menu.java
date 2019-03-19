package View;

import Controller.Protocol;
import sun.jvm.hotspot.memory.Space;

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
                        System.out.println("Has seleccionado la opcion 4");
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
                        break;
                    case "IDCK":
                        this.takeTheInitialCards();
                        break;
                    case "CARD":
                        this.takeAcard();
                        break;
                    case "SHOW":
                        break;

                    case "WINS":

                        break;
                    case "ERRO":
                        this.protocol.handlerError();
                        break;
                    default:
                        System.err.println("It ins't a valid command. closing the game");
                        //set running to false

                        break;
                }

            } catch (IOException ex) {
                System.err.println("Error reading the Socket: " + ex.getMessage());
            }
        }
    }

    private void takeAcard() throws IOException {
        String space = this.protocol.read_sp();
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
        System.out.println("    Entry the amount of cash:");
        System.out.println("--------------------------------");
        cash = this.scanner.nextInt();
        this.protocol.sendCash(cash);
    }

    private void takeTheInitialCards() throws IOException {
        System.out.println("--------------------------------");
        for (int i = 0; i < 2; i++){
            System.out.println("    Card " +  this.protocol.takeACard());
        }
        System.out.println("    Your amount is " + this.protocol.handAmount());
        System.out.println("--------------------------------");
        System.out.println("    Select a number:");
        System.out.println("    1. Ask for a new card");
        System.out.println("    2. Double the bet");
        System.out.println("--------------------------------");
        this.getAction();
    }

    private void getAction() throws IOException {
        int opcio = this.scanner.nextInt();

        if (opcio == 1) {
            this.protocol.sendHitt();
        }else if(opcio == 2){
            this.protocol.sendBet();
        }
    }
}
