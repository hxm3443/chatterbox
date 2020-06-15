package common;

import java.io.*;
import java.net.Socket;

/**
 * This is the abstract Reader class which is used by both the client and the server.
 * File Name: Reader.java
 *
 * @author Himani Munshi
 */
public abstract class Reader implements Runnable {

    protected BufferedReader in;
    protected PrintWriter out;
    protected Socket socket;

    /**
     * Constructor
     *
     * @param socket used to read
     */
    public Reader(Socket socket) {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            this.socket = socket;

        } catch (IOException ioe) {
            System.out.println(ioe);
            System.exit(1);
        }
    }

    /**
     * This method keeps on reading the input and handles the message accordingly.
     */
    @Override
    public void run() {
        while (true) {
            try {
                String message = in.readLine();
                boolean isBreak = handle(message);

                if (!isBreak) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.close();
    }

    /**
     * abstract method which is common to ClientThread and SocketHandler classes (having different implementations).
     *
     * @param message
     * @return
     */
    public abstract boolean handle(String message);
}
