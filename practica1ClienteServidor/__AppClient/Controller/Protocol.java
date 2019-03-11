

public class Protocol {
    private ComUtils comUtils;
    private Socket socket;
    private InetAddress serverAddress;
    private BlackJack blackJack;
    private String message;

    public Protocol(String server, int port) {
        this.serverAddress = InetAddress.getByName(server);
        this.socket = new Socket(this.serverAddress, port);
        this.comUtils = new ComUtils(this.socket);
    }
}