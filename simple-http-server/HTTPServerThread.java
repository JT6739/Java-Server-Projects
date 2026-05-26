/*
 * Jose Trevizo
 * HTTP Server Thread
 * Handles each client connection in a separate thread.
 * Processes HTTP requests and sends response status codes with file content.
 */

import java.io.*;
import java.net.*;
import java.util.Date;

public class HTTPServerThread extends Thread {
    private Socket clientSocket = null;

    public HTTPServerThread(Socket socket) {
        super("HTTPServerThread");
        clientSocket = socket;
    }

    public void run() {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String requestLine;

            while ((requestLine = in.readLine()) != null) {
                System.out.println("Request line: " + requestLine);
                String headerLine;
                String userAgent = "";
                String host = "";
                while ((headerLine = in.readLine()) != null && !headerLine.equals("")) {
                    System.out.println(headerLine);
                    if (headerLine.startsWith("Host:")) host = headerLine;
                    if (headerLine.startsWith("User-Agent:")) userAgent = headerLine;
                }

                String[] parts = requestLine.split(" ");
                String statusLine;
                String dateHeader = "Date: " + new Date().toString();
                String serverHeader = "Server: CS3700Server/1.0";

                if (parts.length < 3 || !parts[0].equals("GET")) {
                    statusLine = "HTTP/1.1 400 Bad Request";
                    out.print(statusLine + "\r\n");
                    out.print(dateHeader + "\r\n");
                    out.print(serverHeader + "\r\n");
                    out.print("\r\n");
                    out.flush();
                    System.out.println("Sent: " + statusLine);

                } else {
                    String url = parts[1];
                    String filename = url.startsWith("/") ? url.substring(1) : url;

                    File requestedFile = new File(filename);

                    if (!requestedFile.exists() || !requestedFile.canRead()) {
                        statusLine = "HTTP/1.1 404 Not Found";
                        out.print(statusLine + "\r\n");
                        out.print(dateHeader + "\r\n");
                        out.print(serverHeader + "\r\n");
                        out.print("\r\n");
                        out.flush();
                        System.out.println("Sent: " + statusLine);

                    } else {
                        statusLine = "HTTP/1.1 200 OK";
                        out.print(statusLine + "\r\n");
                        out.print(dateHeader + "\r\n");
                        out.print(serverHeader + "\r\n");
                        out.print("\r\n");  

                        BufferedReader fileIn = new BufferedReader(new FileReader(requestedFile));
                        String fileLine;
                        while ((fileLine = fileIn.readLine()) != null) {
                            out.print(fileLine + "\r\n");
                        }
                        fileIn.close();

                        out.print("\r\n");
                        out.print("\r\n");
                        out.print("\r\n");
                        out.print("\r\n");
                        out.flush();
                        System.out.println("Sent: " + statusLine + " with file: " + filename);
                    }
                }
            }

            out.close();
            in.close();
            clientSocket.close();
            System.out.println("Client connection closed.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}