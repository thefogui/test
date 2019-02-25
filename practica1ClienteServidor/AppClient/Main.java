import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {

    private static final int EXIT = 3;
    private static final int AUTOMATIC_CLIENT = 1;
    private static final int MANUAL_CLIENT = 2;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int option = 0;

        while(option != EXIT) {
            System.out.println("1. Automatic client");
            System.out.println("2. Sub menu client");
            System.out.println("3. Exit");

            try {

                System.out.println("Choose an option");
                option = scanner.nextInt();

                switch (option) {
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
