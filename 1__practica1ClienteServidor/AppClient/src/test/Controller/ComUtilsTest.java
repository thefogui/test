package test.Controller;

import Controller.ComUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComUtilsTest {

    @Test
    void readCommand() {
        try {
            File file = new File("test.txt");

            FileInputStream fileInputStream = new FileInputStream("test.txt");

            if (file.createNewFile()) {
                System.out.println("File is created!");
            } else {
                System.out.println("File already exists.");
            }
            FileOutputStream fileOutputStream = new FileOutputStream("test.txt" );

            ComUtils comUtils = new ComUtils(new DataInputStream(fileInputStream), new DataOutputStream(fileOutputStream));
            comUtils.write_string("STRT");

            assertEquals("STRT", comUtils.readCommand());

        } catch (IOException ex) {
            System.err.println("Error with the socket " + ex.getMessage());
        }
    }
}