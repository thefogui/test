import View.Menu;

public class Client {


    public static void main(String[] args) {
        String server = null;
        int numPort = 0;

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

        Menu menu = new Menu(server, numPort);
        menu.mainMenu();
    }
}
