package client;

import common.ChatterboxProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is the class (thread) which connects the Client to the Console.
 * File Name: ConsoleHandler.java
 * @author Himani Munshi
 */
public class ConsoleHandler implements Runnable {
    private PrintWriter out;
    private BufferedReader reader;

    /**
     * Constructor
     * @param out for writing to the server
     */
    public ConsoleHandler(PrintWriter out) {
        this.out = out;
        reader = new BufferedReader(new InputStreamReader(System.in));

    }

    /**
     * This method calls readUsername() and readUserInput() methods and is responsible for closing the 'reader'.
     */
    public void run() {
        readUsername();
        readUserInput();
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads 'Username' entered by the user and writes it to the server using 'out'.
     */
    public void readUsername() {
        System.out.print("Username: ");
        String username = null;
        try {
            username = reader.readLine();
            String clientMessage = ChatterboxProtocol.CONNECT + ChatterboxProtocol.SEPARATOR + username;
            out.println(clientMessage);
            System.out.println("Welcome to Chatterbox! Type '/help' to see a list of commands.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads user input using 'reader', checks whether the user wants to quit or not and calls the readUserCommand() method
     * if the user is not quitting.
     */
    public void readUserInput() {
        try {
            String input = reader.readLine();
            while (true) {
                if (input.equals("/quit")) {
                    String ans = quit();
                    if (ans.equals("y")) {
                        System.out.println("GoodBye!");
                        out.println(ChatterboxProtocol.DISCONNECT);
                        break;
                    } else if (ans.equals("n")) {
                        input = reader.readLine();
                        continue;
                    }
                }
                String[] lst = input.split(" ");
                readUserCommand(lst);
                input = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads user input. The user uses simple commands that are translated into the appropriate messages by the
     * Chatterbox client program. The following cases in the switch case lists all of the available commands.
     * @param userCommand user input
     */
    public void readUserCommand(String[] userCommand) {
        switch (userCommand[0]) {
            case "/help":
                help();
                break;
            case "/c":
                sendMessage(userCommand);
                break;
            case "/w":
                sendWhisper(userCommand[1], userCommand);
                break;
            case "/list":
                listUsers();
                break;
            default:
                System.out.println("Invalid input!");
        }

    }

    /**
     * Displays a list of the available commands.
     */
    public void help() {
        System.out.println("/help - displays this message\n" + "/quit - quit Chatterbox\n" + "/c <message> - send a message to all currently connected users\n"
                + "/w - <recipient> <message> - send a private message to the recipient\n" + "/list - display a list of currently connected users");
    }

    /**
     * Prompts the user to ask if they are sure that they want to quit.
     * @return "y" if user decides to quit otherwise "n"
     */
    public String quit() {
        System.out.print("Are you sure (y/n): ");
        String yesOrNo = null;
        try {
            yesOrNo = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return yesOrNo;

    }

    /**
     * Sends the message typed in by the user to the Chatterbox server to be broadcast to all currently connected users.
     * @param message list containing the user input splitted on the basis of whitespace
     */
    public void sendMessage(String[] message) {
        String resultString = ChatterboxProtocol.SEND_CHAT + ChatterboxProtocol.SEPARATOR;
        List<String> list = new ArrayList<String>(Arrays.asList(message));
        list.remove("/c");
        for (String element : list) {
            resultString += element + " ";
        }
        out.println(resultString);
    }

    /**
     * Sends a private/direct message to the specified recipient.
     * @param recipient the user to whom the message has been sent
     * @param message list containing the user input splitted on the basis of whitespace
     */
    public void sendWhisper(String recipient, String[] message) {
        String resultString = ChatterboxProtocol.SEND_WHISPER + ChatterboxProtocol.SEPARATOR + recipient + ChatterboxProtocol.SEPARATOR;
        List<String> list = new ArrayList<String>(Arrays.asList(message));
        list.remove("/w");
        list.remove(0);
        for (String element : list) {
            resultString += element + " ";
        }
        out.println(resultString);
    }

    /**
     * Used to request that the Chatterbox server to return a list of currently connected users.
     */
    public void listUsers() {
        out.println(ChatterboxProtocol.LIST_USERS);
    }

}
