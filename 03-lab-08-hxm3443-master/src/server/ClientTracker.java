package server;

import common.ChatterboxProtocol;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class maps userNames with their respective sockets in a HashMap and has methods like add, remove to add a client
 * and remove a client etc. In a nutshell, keeps track of all the running clients.
 * File Name: ClientTracker.java
 * @author Himani Munshi
 */
public class ClientTracker {
    private HashMap<String, Socket> clientTracker;

    /**
     * Constructor
     */
    public ClientTracker() {
        clientTracker = new HashMap<>();
    }

    /**
     * Checks if the user exists, if not then adds it to the HashMap and is also responsible for notifying all the running
     * clients that a new user has been added.
     * @param username name of the user to be added
     * @param socket used to read
     * @return true if the username did not already exist in the table, otherwise false
     */
    public synchronized boolean addClient(String username, Socket socket) {
        if (!clientTracker.containsKey(username)) {
            clientTracker.put(username, socket);
            for (String eachUser : clientTracker.keySet()) {
                if (!eachUser.equals(username)) {
                    Socket socketConnection = clientTracker.get(eachUser);
                    PrintWriter out = null;
                    try {
                        out = new PrintWriter(socketConnection.getOutputStream(), true);
                        out.println(ChatterboxProtocol.USER_JOINED + ChatterboxProtocol.SEPARATOR + username);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Removes a user as soon as the user has been disconnected from the server and is also responsible for notifying
     * all the running clients that the user has left.
     * @param username name of the user to be removed
     */
    public synchronized void removeClient(String username) {
        if (clientTracker.containsKey(username)) {
            clientTracker.remove(username);
        }

        for (String eachUser : clientTracker.keySet()) {
            Socket socketConnection = clientTracker.get(eachUser);
            PrintWriter out = null;
            try {
                out = new PrintWriter(socketConnection.getOutputStream(), true);
                out.println(ChatterboxProtocol.USER_LEFT + ChatterboxProtocol.SEPARATOR + username);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is responsible for displaying the chat message sent by a specific user to the console of every other
     * user who is connected.
     * @param message server message after interpreting the client's message
     */
    public synchronized void accessAllClients(String message) {
        //using 'for loop' to loop through all threads except the current one
        try {
            for (String name : clientTracker.keySet()) {
                Socket socket = clientTracker.get(name);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(message);
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    /**
     * This method is responsible for displaying the message sent by a specific user to a specific user
     * @param username receiver's name to whom the message has been sent
     * @param message server message after interpreting the client's message
     */
    public synchronized boolean accessOneClient(String username, String message) {
        if (!clientTracker.containsKey(username)) {
            return false;
        }
        Socket socket = clientTracker.get(username);
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
        return true;
    }

    /**
     * creates a list of userNames for all the running clients
     * @return list containing the userNames of all the running clients
     */
    public synchronized ArrayList<String> listClients() {
        ArrayList<String> usernames = new ArrayList<>();
        for (String username : clientTracker.keySet()) {
            usernames.add(username);
        }
        return usernames;
    }
}
