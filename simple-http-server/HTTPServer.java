/*
 * Jose Trevizo
 * HTTP Server Program
 * Java multithreaded server application using TCP sockets.
 * Server handles HTTP GET requests and returns files to connected clients. 
 */

import java.io.*;
import java.net.*;

public class HTTPServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(5340);
            System.out.println("HTTP Server listening on port 5340...");
        } catch (IOException e) {
            System.err.println("Could not listen on port: 5340");
            System.exit(-1);
        }

        while (listening) {
            new HTTPServerThread(serverSocket.accept()).start();
            System.out.println("New client connected - thread started.");
        }

        serverSocket.close();
    }
}