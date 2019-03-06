package main.controller;

import java.io.*;
import java.net.*;
import java.util.*;

public class Connect {
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket socket;
    private Date date;

    public Connect() {

    }

    private openSocket(){
        try {
            socket = new Socket(argv[0].trim(), 3000);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch(Exception e){
            
        }
    }
}