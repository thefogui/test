package main.controller;

import java.io.*;
import java.net.*;
import java.util.*;

public class Connect {
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket socket;
    private Date date;
    private String userName;

    public Connect(Int port, String userName) {
        this.userName = userName;
        socket = new Socket(3000);
    }

    private openSocket(){
        try {

            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch(Exception e){

        }
    }
}