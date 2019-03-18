import java.io.IOException;
import java.util.Scanner;
import java.util.InputMismatchException;
import Controller.*;

public class Client {
    private static final int CONNECT_TO_SERVER = 1;
    private static final int AUTOMATIC_CLIENT = 2;
    private static final int MANUAL_CLIENT = 3;
    private static final int EXIT = 4;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int option = 0;
        String input = null;
        String server = null;
        int numPort = 0;
        int username = 0;

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
                        System.out.println("Connecting to host...");


                        if (args.length  < 1) {
                            System.err.println("Invalid entry for the port");
                        }

                        if (args.length < 2) {
                            System.err.println("Invalid entry for app ");
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
                        }

                        try {
                            System.out.println("--------------------------------");
                            System.out.println("Username:");
                            System.out.println("--------------------------------");
                            username = scanner.nextInt();
                        } catch (Exception e) {
                            System.err.println("Not a valid number!");
                        }

                        try {
                            Protocol protocol = new Protocol(server, numPort, username);
                            protocol.start();
                        }catch (IOException e) {
                            System.err.println("Can't start the protocol " + e.getMessage());
                        }

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
}
