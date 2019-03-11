import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {

    private static final int EXIT = 4;
    private static final int AUTOMATIC_CLIENT = 2;
    private static final int MANUAL_CLIENT = 3;
    private static final int CONNECT_TO_SERVER = 1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int option = 0;
        String input;
        String server;
        String numPort;

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
                        input = scanner.next();
                        Connect connect = new Connect(arga[0], input);
                        if (args.length  < 1) {
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
                        }

                        Protocol protocol = new Protocol(server, numPort);
                        protocol.start();

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
