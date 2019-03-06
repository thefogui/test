package main.controller;

import com.sun.org.apache.bcel.internal.generic.Select;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ServerSelected {
    private Selector select;
    private ServerSocketChannel server;
    private SocketChannel client;
    private SelectionKey serverKey;

    public ServerSelected() throws IOException {
        this.select = Selector.open();
        server = ServerSocketChannel.open();
        serverKey = server.register(select,SelectionKey.OP_ACCEPT);
    }

    public void select() throws IOException {
        while(true) {
            select.select();

            Set keys = select.selectedKeys();

            for(Iterator i = keys.iterator(); i.hasNext();) {
                SelectionKey key = (SelectionKey) i.next();
                i.remove();

                if(key == serverKey) {
                    if (key.isAcceptable()) {
                        client = server.accept();
                        client.configureBlocking(false);
                        SelectionKey clientkey = client.register(select, SelectionKey.OP_ACCEPT);

                    } else {
                        client = (SocketChannel) key.channel();
                       /* switch (){

                        }*/
                    }
                }
            }
        }
    }
}
