package server;

import common.ChatterboxProtocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class represents the server side of the network.
 * File Name: Server.java
 * @author Himani Munshi
 */
public class Server {
    private ClientTracker clientTracker;

    /**
     * Constructor
     */
    public Server() {
        clientTracker = new ClientTracker();
    }

    /**
     * This method starts the server and establishes the socket connection once a client connects with the server.
     */
    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(ChatterboxProtocol.PORT);) {

            while (true) {
                System.out.println("Waiting for connections on port " + ChatterboxProtocol.PORT);
                Socket socket = serverSocket.accept();
                System.out.println("ChatterboxClient connection received from " + socket.getInetAddress());
                ClientThread clientThread = new ClientThread(socket, clientTracker);
                Thread thread = new Thread(clientThread);
                thread.start();
            }
        } catch (IOException ex) {
            System.err.println(ex);
            System.exit(1);
        }
    }

    /**
     * main method that calls the startServer() method.
     * @param args ignored
     */
    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
}
