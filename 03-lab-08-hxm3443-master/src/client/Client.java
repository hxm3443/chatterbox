package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * This class represents the client side of the network.
 * File Name: Client.java
 * @author Himani Munshi
 */
public class Client {

    /**
     * main method responsible for creating the two threads which are associated with the client (one responsible for
     * interacting with the socket and the other with the console).
     * @param args ignored
     */
    public static void main(String[] args) {
        System.out.print("Chatterbox server host: ");
        Scanner scanner1 = new Scanner(System.in);
        String host = scanner1.next();

        System.out.print("Chatterbox server port: ");
        Scanner scanner2 = new Scanner(System.in);
        String port = scanner2.next();

        try (Socket serverConnection = new Socket(host, Integer.parseInt(port));
             PrintWriter out = new PrintWriter(serverConnection.getOutputStream(), true);) {

            ConsoleHandler consoleHandlerThread = new ConsoleHandler(out);
            SocketHandler socketHandlerThread = new SocketHandler(serverConnection);
            Thread thread1 = new Thread(consoleHandlerThread);
            Thread thread2 = new Thread(socketHandlerThread);
            thread1.start();
            thread2.start();
            thread1.join();
            thread2.join();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
