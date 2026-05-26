/* Jose Trevizo 
 * Java TCP client application using sockets.
 * Client sends HTTP GET requests to a server and measures response time.
 * Saves the returned file content when the server responds with 200 OK. 
 */

import java.io.*;
import java.net.*;

public class HTTPClient {
    public static void main(String[] args) throws IOException {

        BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter the DNS name or IP address of the HTTP server: ");
        String hostname = userIn.readLine();

        Socket tcpSocket = null;
        long connStart = System.currentTimeMillis();
        try {
            tcpSocket = new Socket(hostname, 5340); //set up new socket with hostname from user and port 5340
        } catch (UnknownHostException e) {
            System.out.println("Don't know about host: " + hostname);
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Couldn't get I/O for the connection to: " + hostname);
            System.exit(1);
        }
        long connEnd = System.currentTimeMillis();
        double connRTT = connEnd - connStart;
        System.out.printf("TCP connection established. RTT = %.3f ms%n", connRTT);

        PrintWriter socketOut = new PrintWriter(tcpSocket.getOutputStream(), true);
        BufferedReader socketIn = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));

        String continueChoice = "yes";

        while (continueChoice.equalsIgnoreCase("yes") || continueChoice.equalsIgnoreCase("y")) {

            
            System.out.print("Enter HTTP method: ");
            String method = userIn.readLine();

            System.out.print("Enter the name of the file requested: ");
            String filename = userIn.readLine();

            System.out.print("Enter HTTP Version: ");
            String version = userIn.readLine();

            System.out.print("Enter User-Agent: ");
            String userAgent = userIn.readLine();

  
            String requestLine = method + " /" + filename + " " + version + "\r\n";
            String hostLine    = "Host: " + hostname + "\r\n";
            String agentLine   = "User-Agent: " + userAgent + "\r\n";
            String emptyLine   = "\r\n";

            long reqStart = System.currentTimeMillis();
            System.out.println("\nSent information:");
            System.out.println(requestLine);
            System.out.println(hostLine);
            System.out.println(agentLine);
            System.out.println(emptyLine);

            socketOut.print(requestLine);
            socketOut.print(hostLine);
            socketOut.print(agentLine);
            socketOut.print(emptyLine);
            socketOut.flush();

            String fromServer;
            boolean headersDone = false;
            int emptyLineCount = 0;
            PrintWriter fileOut = null;
            boolean is200OK = false;

            while ((fromServer = socketIn.readLine()) != null) {

                if (!headersDone) {
                    // Display status line and header lines
                    System.out.println(fromServer);

                    if (fromServer.contains("200 OK")) {
                        is200OK = true;
                    }

                    if (fromServer.equals("")) {
                        headersDone = true;
                        long reqEnd = System.currentTimeMillis();
                        double httpRTT = reqEnd - reqStart;
                        System.out.printf("RTT of HTTP query = %.3f ms%n", httpRTT);
                        if (is200OK) {
                            fileOut = new PrintWriter(new FileWriter(filename));
                        }
                    }
                } else {
                    // We are in the entity body
                    if (is200OK) {
                        if (fromServer.equals("")) {
                            emptyLineCount++;
                            if (emptyLineCount >= 4) {
                                break;
                            }
                        } else {
                            emptyLineCount = 0;
                            if (fileOut != null) {
                                fileOut.println(fromServer);
                            }
                        }
                    } else {
                        
                        break;
                    }
                }
            }

            if (fileOut != null) {
                fileOut.close();
                System.out.println("Entity body saved to: " + filename);
            }

           
            System.out.print("Do you want to continue? (yes/no): ");
            continueChoice = userIn.readLine();
        }

    
        socketOut.close();
        socketIn.close();
        userIn.close();
        tcpSocket.close();
        System.out.println("Connection closed");
    }
}