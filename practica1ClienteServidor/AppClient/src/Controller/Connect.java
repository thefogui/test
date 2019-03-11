package Controller;

import java.io.*;
import java.net.*;
import java.util.*;

public class Connect {
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket socket;
    private Date date;
    private String userName;

    public Connect(String port, String userName) {
        this.userName = userName;
        socket = new Socket();
    }

    private void openSocket(){
        try {

            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch(Exception e){

        }
    }
}