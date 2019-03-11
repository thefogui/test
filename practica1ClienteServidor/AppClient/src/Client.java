import java.io.IOException;
import java.util.Scanner;
import java.util.InputMismatchException;
import Controller.*;

public class Client {

    private static final int EXIT = 4;
    private static final int AUTOMATIC_CLIENT = 2;
    private static final int MANUAL_CLIENT = 3;
    private static final int CONNECT_TO_SERVER = 1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int option = 0;
        String input = null;
        String server = null;
        int numPort = 0;

        while(option != EXIT) {
            System.out.println("1. Connect to server");
            System.out.println("2. Automatic client");
            System.out.println("3. Sub menu client");
            System.out.println("4. Exit");

            try {

                System.out.println("Choose an option");
                option = scanner.nextInt();

                switch (option) {
                    case CONNECT_TO_SERVER:
                        System.out.println("Connectiong to host...");


                        /*if (args.length  < 1) {
                            System.err.println("Invalid entry for the port");
                        }

                        if (args.length < 2) {
                            System.out.println("Invalid entry for app ");
                            System.exit(1);
                        }

                        for (int i = 0; i < args.length; i += 2){
                            switch(args[i]){
                                case "-s":
                                    server = args[i+1];
                                    break;
                                case "-p":
                                    numPort = Integer.parseInt(args[i+1]);
                                    break;
                            }
                        }*/

                        System.out.println("Insert the server IP");
                        server = scanner.next();
                        System.out.println("Insert the server Port");
                        numPort = scanner.nextInt();

                        try {
                            Protocol protocol = new Protocol(server, numPort);
                            protocol.start();
                        }catch (IOException e) {
                            System.err.println("Can't start the protocol " + e.getMessage());
                        }

                    case AUTOMATIC_CLIENT:
                        System.out.println("Has seleccionado la opcion 1");
                        break;
                    case MANUAL_CLIENT:
                        System.out.println("Has seleccionado la opcion 2");
                        break;
                    case EXIT:
                        System.out.println("Has seleccionado la opcion 3");
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
}
