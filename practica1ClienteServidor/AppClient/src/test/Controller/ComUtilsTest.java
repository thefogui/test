package test.Controller;

import Controller.ComUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

class ComUtilsTest {

    @Test
    void readCommand() {
        try {
            System.out.println(1);
            ComUtils comUtils = new ComUtils(new Socket("localhost", 1312));
            System.out.println(2);
            String readString = comUtils.readCommand();
            System.out.println(readString);
        } catch (IOException ex) {
            System.err.println("Error with the socket");
        }
    }
}