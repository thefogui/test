package main.java.com.controller;

import java.io.IOException;
import java.net.Inet4Address;

public class ServerSocket extends java.net.ServerSocket {

    public ServerSocket(Inet4Address address, int port) throws IOException {
        super(port);
    }

}
