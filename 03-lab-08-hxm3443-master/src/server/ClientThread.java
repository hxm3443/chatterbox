package server;

import common.ChatterboxProtocol;
import common.Reader;

import java.net.Socket;
import java.util.ArrayList;

/**
 * This class is responsible for reading and dealing with the data associated with a client at a time.
 * File Name: ClientThread.java
 * @author Himani Munshi
 */
public class ClientThread extends Reader implements Runnable {

    private ClientTracker clientTracker;
    private String username;

    /**
     * Constructor
     * @param socket used to read
     * @param clientTracker object of class ClientTracker which contains the hashTable and desired methods
     */
    public ClientThread(Socket socket, ClientTracker clientTracker) {
        super(socket);
        this.clientTracker = clientTracker;
    }

    /**
     * This method is responsible for reading and parsing the client message and also printing to the server console.
     * @param lst the entire list containing the client message that has been splitted on the basis of "::" (SEPARATOR).
     */
    public boolean interpretClientMessage(String[] lst) {
        switch (lst[0]) {
            case ChatterboxProtocol.CONNECT:
                this.username = lst[1];
                boolean isConnected = clientTracker.addClient(username, socket);
                if (isConnected) {
                    out.println(ChatterboxProtocol.CONNECTED);
                    System.out.println(">>" + username + ": " + ChatterboxProtocol.CONNECTED);
                } else {
                    out.println(ChatterboxProtocol.FATAL_ERROR + ChatterboxProtocol.SEPARATOR + "The user already exists.");
                    System.out.println(">>" + username + ": " + ChatterboxProtocol.FATAL_ERROR + ChatterboxProtocol.SEPARATOR + "The user already exists.");
                    return false;
                }
                break;
            case ChatterboxProtocol.SEND_CHAT:
                if (lst.length == 2) {
                    String newMsg = ChatterboxProtocol.CHAT_RECEIVED + ChatterboxProtocol.SEPARATOR + username + ChatterboxProtocol.SEPARATOR;
                    String message = lst[1];
                    newMsg += message;
                    clientTracker.accessAllClients(newMsg);
                } else {
                    String newErrorMsg = ChatterboxProtocol.ERROR + ChatterboxProtocol.SEPARATOR;
                    newErrorMsg += "Unknown or unexpected protocol message: Wrong number of arguments entered--> " + lst[0];
                    out.println(newErrorMsg);
                    System.out.println(">>" + username + ": " + newErrorMsg);
                }
                break;
            case ChatterboxProtocol.SEND_WHISPER:
                if (lst.length == 3) {
                    String newString = ChatterboxProtocol.WHISPER_RECEIVED + ChatterboxProtocol.SEPARATOR + username + ChatterboxProtocol.SEPARATOR;
                    String resultMsg = lst[2];
                    String receiver = lst[1];
                    newString += resultMsg;
                    boolean userExists = clientTracker.accessOneClient(receiver, newString);
                    if (userExists) {
                        out.println(ChatterboxProtocol.WHISPER_SENT + ChatterboxProtocol.SEPARATOR + receiver + ChatterboxProtocol.SEPARATOR + resultMsg);
                        System.out.println(">>" + username + ": " + ChatterboxProtocol.WHISPER_SENT + ChatterboxProtocol.SEPARATOR
                                + receiver + ChatterboxProtocol.SEPARATOR + resultMsg);
                    }
                    else {
                        String newErrorMsg = ChatterboxProtocol.ERROR + ChatterboxProtocol.SEPARATOR;
                        newErrorMsg += "Username " + receiver + " does not exist";
                        out.println(newErrorMsg);
                        System.out.println(">>" + username + ": " + newErrorMsg); }
                } else {
                    String newErrorMsg = ChatterboxProtocol.ERROR + ChatterboxProtocol.SEPARATOR;
                    newErrorMsg += "Unknown or unexpected protocol message: Wrong number of arguments entered--> " + lst[0];
                    out.println(newErrorMsg);
                    System.out.println(">>" + username + ": " + newErrorMsg);
                }
                break;
            case ChatterboxProtocol.LIST_USERS:
                ArrayList<String> listUsers = clientTracker.listClients();
                String result = ChatterboxProtocol.USERS + ChatterboxProtocol.SEPARATOR;
                for (int i = 0; i < listUsers.size(); i++) {
                    if (i != listUsers.size() - 1) {
                        result += listUsers.get(i) + ChatterboxProtocol.SEPARATOR;
                    } else {
                        result += listUsers.get(i);
                    }
                }
                out.println(result);
                System.out.println(">>" + username + ": " + result);
                break;
            default:
                String errorMsg = ChatterboxProtocol.ERROR + ChatterboxProtocol.SEPARATOR;
                errorMsg += "Unknown or unexpected protocol message: " + lst[0];
                out.println(errorMsg);
                System.out.println(">>" + username + ": " + errorMsg);
        }
        return true;
    }

    /**
     * Checks whether the user is disconnected or not, and continues if the user is not disconnected and calls the
     * interpretClientMessage() method.
     * @param message contains the entire message received from the client
     * @return false if the user gets disconnected, otherwise true
     */
    @Override
    public boolean handle(String message) {
        String result = "<<";
        String[] lst = message.split("::");
        if (lst[0].equals(ChatterboxProtocol.CONNECT)) {
            result += "unknown user: " + message;
        } else {

            if (username == null) {
                out.println(ChatterboxProtocol.FATAL_ERROR + ChatterboxProtocol.SEPARATOR + "The username has not been entered.");
                System.out.println(">>" + username + ": " + ChatterboxProtocol.FATAL_ERROR + ChatterboxProtocol.SEPARATOR + "The username has not been entered.");
                return false;
            }
            result += username + ": " + message;

        }
        System.out.println(result);
        if (lst[0].equals(ChatterboxProtocol.DISCONNECT)) {
            clientTracker.removeClient(username);
            out.println(ChatterboxProtocol.DISCONNECTED);
            return false;
        }
        return interpretClientMessage(lst);
    }
}