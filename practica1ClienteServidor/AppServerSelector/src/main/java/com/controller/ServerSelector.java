package main.java.com.controller;


import main.java.com.model.Users;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Set;

public class ServerSelector {
    private Selector selector;
    private ServerSocketChannel server;
    private SocketChannel client;
    private SelectionKey serverKey;
    private static ByteBuffer buffer;
    private Users users;
    private InetSocketAddress addr;
    private ServerThread serverThread;

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

    public void startServer() throws IOException {
        while (true) {
            System.out.println("Waiting for clients...");
            selector.select();

            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> crunchifyIterator = keys.iterator();

            while (crunchifyIterator.hasNext()) {
                SelectionKey myKey = crunchifyIterator.next();

                if (myKey.isAcceptable()) {
                    client = server.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                    serverThread = new ServerThread(client,users);
                }
                crunchifyIterator.remove();
            }
        }
    }

    protected void finalize() throws IOException {
        client.close();
        buffer = null;
    }
}
