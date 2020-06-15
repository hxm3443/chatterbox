package client;

import common.ChatterboxProtocol;
import common.Reader;

import java.net.Socket;

/**
 * This is the class (thread) which connects the Client to the Server.
 * File Name: SocketHandler.java
 * @author Himani Munshi
 */
public class SocketHandler extends Reader implements Runnable {

    /**
     * Constructor
     * @param socket used to read
     */
    public SocketHandler(Socket socket) {
        super(socket);
    }

    /**
     * This method is responsible for reading the messages that the server is passing to it. It parses the message and
     * does the required thing based on the protocol message.
     * @param lst list containing the entire message from the server that has been splitted based on "::" (SEPARATOR).
     */
    public void readServer(String[] lst) {
        switch (lst[0]) {
            case ChatterboxProtocol.CONNECTED:
                break;
            case ChatterboxProtocol.DISCONNECTED:
                break;
            case ChatterboxProtocol.CHAT_RECEIVED:
                String result = lst[2];
                System.out.println(lst[1] + " said: " + result);
                break;
            case ChatterboxProtocol.WHISPER_RECEIVED:
                String resultMsg = lst[2];
                System.out.println(lst[1] + " whispers to you: " + resultMsg);
                break;
            case ChatterboxProtocol.WHISPER_SENT:
                String resultString = lst[2];
                System.out.println("You whispered to " + lst[1] + ": " + resultString);
                break;
            case ChatterboxProtocol.USERS:
                System.out.println("The following users are connected:");
                for (int i = 1; i < lst.length; i++) {
                    System.out.println(lst[i]);
                }
                break;
            case ChatterboxProtocol.USER_JOINED:
                System.out.println("A user has joined the Chatterbox server: " + lst[1]);
                break;
            case ChatterboxProtocol.USER_LEFT:
                System.out.println("A user has left the server: " + lst[1]);
                break;
            case ChatterboxProtocol.ERROR:
                System.out.println(lst[1]);
                break;
            case ChatterboxProtocol.FATAL_ERROR:
                System.out.println(lst[1]);
                System.exit(1);
                break;
        }
    }

    /**
     * Checks whether the user is disconnected or not, and continues if the user is not disconnected and calls the
     * readServer() method.
     * @param message contains the entire message that the server passed to it
     * @return false if the user gets disconnected, otherwise true
     */
    @Override
    public boolean handle(String message) {
        String[] lst = message.split("::");
        if (lst[0].equals(ChatterboxProtocol.DISCONNECTED)) {
            return false;
        }
        readServer(lst);
        return true;
    }
}

