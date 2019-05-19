package main.java.com.controller;


import main.java.com.model.Users;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Set;

/**
 * ServerSelector class it start the server and waits to clients connections.
 *
 * @author Vitor Carvalho and Ivet Aymerich.
 */
public class ServerSelector {
    private Selector selector;
    private ServerSocketChannel server;
    private SocketChannel client;
    private SelectionKey serverKey;
    private Users users;
    private InetSocketAddress addr;
    private Protocol protocol;

    /**
     * Constructor of the class
     * @param port port that the server is allocated
     * @throws IOException error creating the socket.
     */
    public ServerSelector(int port) throws IOException {
        selector = Selector.open();
        server = ServerSocketChannel.open();

        addr = new InetSocketAddress("localhost",port);
        server.bind(addr);

        server.configureBlocking(false);
        int ops = server.validOps();
        serverKey = server.register(selector, ops, null);

        this.users = new Users();
    }

    /**
     * Starts the execution of the server
     * @throws IOException error creating the socket.
     */
    public void startServer() throws IOException {
        while (true) {
            System.out.println("Waiting for clients...");
            selector.select();

            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> crunchifyIterator = keys.iterator();

            while (crunchifyIterator.hasNext()) {
                SelectionKey myKey = crunchifyIterator.next();

                if (myKey==serverKey){
                    if (myKey.isAcceptable()) {
                        client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                        this.protocol = new Protocol(client, this.users);
                        myKey.attach(protocol);
                        Object obj = myKey.attachment();
                        client.register(selector,SelectionKey.OP_READ, obj);
                    }
                } else if(myKey.isReadable()){
                    client = (SocketChannel) myKey.channel();
                    Protocol estat = (Protocol) myKey.attachment();
                    estat.readSocket();
                }
                    crunchifyIterator.remove();
            }
        }
    }

    protected void finalize() throws IOException {
        client.close();
    }
}
