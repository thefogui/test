package main.controller;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            ServerSelected select = new ServerSelected();
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}
