package main.java.com.controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ComUtils {
    private final int STRSIZE = 40;

    //private DataInputStream dataInputStream;
    //private DataOutputStream dataOutputStream;
    private ByteBuffer byteBuffer;
    private SocketChannel socket;

    public ComUtils(SocketChannel socket) throws IOException {
        //dataInputStream = new DataInputStream(socket.getInputStream());
        //dataOutputStream = new DataOutputStream(socket.getOutputStream());
        byteBuffer = ByteBuffer.allocate(256);
        this.socket = socket;
    }

    public int read_int32() throws IOException {
        byte bytes[] = read_bytes(4);

        return bytesToInt32(bytes, Endianness.BIG_ENNDIAN);
    }

    public void write_int32(int number) throws IOException {
        byte bytes[] = int32ToBytes(number, Endianness.BIG_ENNDIAN);
        byteBuffer = ByteBuffer.wrap(bytes, 0, 4);
        socket.write(byteBuffer);
    }

    public String read_string() throws IOException {
        String result;
        byte[] bStr = new byte[4];
        char[] cStr = new char[4];

        bStr = read_bytes(4);

        for(int i = 0; i < 4;i++)
            cStr[i]= (char) bStr[i];

        result = String.valueOf(cStr);

        return result.trim();
    }

    public void write_string(String str) throws IOException {
        int numBytes, lenStr;
        byte bStr[] = new byte[4];

        lenStr = str.length();

        if (lenStr > 4)
            numBytes = 4;
        else
            numBytes = lenStr;

        for(int i = 0; i < numBytes; i++)
            bStr[i] = (byte) str.charAt(i);

        for(int i = numBytes; i <4; i++)
            bStr[i] = (byte) ' ';

        byteBuffer = ByteBuffer.wrap(bStr, 0, 4);
        socket.write(byteBuffer);
    }

    private byte[] int32ToBytes(int number, Endianness endianness) {
        byte[] bytes = new byte[4];

        if(Endianness.BIG_ENNDIAN == endianness) {
            bytes[0] = (byte)((number >> 24) & 0xFF);
            bytes[1] = (byte)((number >> 16) & 0xFF);
            bytes[2] = (byte)((number >> 8) & 0xFF);
            bytes[3] = (byte)(number & 0xFF);
        }
        else {
            bytes[0] = (byte)(number & 0xFF);
            bytes[1] = (byte)((number >> 8) & 0xFF);
            bytes[2] = (byte)((number >> 16) & 0xFF);
            bytes[3] = (byte)((number >> 24) & 0xFF);
        }
        return bytes;
    }

    /* Passar de bytes a enters */
    private int bytesToInt32(byte bytes[], Endianness endianness) {
        int number;

        if(Endianness.BIG_ENNDIAN == endianness) {
            number=((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
                    ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        }
        else {
            number=(bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) |
                    ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);
        }
        return number;
    }

    //llegir bytes.
    private byte[] read_bytes(int numBytes) throws IOException {
        int len = 0;
        ByteBuffer bStr = ByteBuffer.allocate(numBytes);
        int bytesread = 0;
        do {
            bytesread = socket.read(bStr);
            if (bytesread == -1)
                throw new IOException("Broken Pipe");
            len += bytesread;
        } while (len < numBytes);
        return bStr.array();

    }

    /* Llegir un string  mida variable size = nombre de bytes especifica la longitud*/
    public  String read_string_variable(int size) throws IOException {
        String result;
        byte[] bStr = new byte[size];
        char[] cStr = new char[size];

        bStr = read_bytes(size);

        for(int i = 0; i < size;i++)
            cStr[i]= (char) bStr[i];

        result = String.valueOf(cStr);

        return result;
    }

    /* Escriure un string mida variable, size = nombre de bytes especifica la longitud  */
    /* String str = string a escriure.*/
    public void write_string_variable(int size,String str) throws IOException {
        int numBytes, lenStr;
        byte bStr[] = new byte[size];

        lenStr = str.length();

        if (lenStr > size)
            numBytes = size;
        else
            numBytes = lenStr;

        for(int i = 0; i < numBytes; i++)
            bStr[i] = (byte) str.charAt(i);

        for(int i = numBytes; i <size; i++)
            bStr[i] = (byte) ' ';

        byteBuffer = ByteBuffer.wrap(bStr, 0, size);
        socket.write(byteBuffer);
    }

    public void write_SP() throws IOException {
        char SP = ' ';
        this.writeChar(SP);
    }

    public void writeCommand(String command) throws IOException {
        this.write_string(command);
    }

    public String readCommand() throws IOException {
        return this.read_string();
    }

    public String read_Char() throws IOException {
        String str;
        byte[] bStr = new byte[1];
        char cStr;

        bStr = read_bytes(1);
        cStr = (char) bStr[0];

        str = String.valueOf(cStr);
        return str;
    }

    public void writeChar(char c) throws IOException{
        byte[] bStr = new byte[1];
        bStr[0] = (byte) c;
        //this.dataOutputStream.write(bStr, 0, 1);
        byteBuffer = ByteBuffer.wrap(bStr, 0, 1);
        socket.write(byteBuffer);
    }

    public void writeErrorMessage(char c1, char c2, int size,String message) throws IOException {
        this.write_SP();
        this.writeChar(c1);
        this.writeChar(c2);
        this.write_string_variable(size, message); //maximum size of
        //the string is 99 characters
    }

    public String readErrorMessage() throws IOException {
        char sp = this.read_Char().charAt(0);
        char c1 = this.read_Char().charAt(0);
        char c2 = this.read_Char().charAt(0);
        int size = Integer.parseInt(new String(String.valueOf(c1) + String.valueOf(c2)));
        return this.read_string_variable(size);
    }

    public enum Endianness {
        BIG_ENNDIAN,
        LITTLE_ENDIAN
    }
}