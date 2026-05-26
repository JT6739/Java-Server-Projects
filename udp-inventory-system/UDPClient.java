/*
 * Jose Trevizo
 * UDP Inventory Lookup System
 * Java client application using UDP sockets.
 * Client requests product information by item ID and measures RTT
 * Client talks to Server program from Client Linux Server to Server Linux Server
 */ 

import java.io.*;
import java.net.*;
import java.util.*;

public class UDPClient {

    public static void printTable (){
        System.out.println("Item ID         Item Description");
        System.out.println("00001           New Inspiron 15");
        System.out.println("00002           New Inspiron 17");
        System.out.println("00003           New Inspiron 15R");
        System.out.println("00004           New Inspiron 15z Ultrabook");
        System.out.println("00005           XPS 14 Ultraboo");
        System.out.println("00006           New XPS 12 UltrabookXPS");
    }

    public static void main(String[] args) throws IOException {

    
        // creat a UDP socket
        DatagramSocket udpSocket = new DatagramSocket();

        BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;
        String fromUser;
        System.out.println("Enter DNS or IP Address: ");
        while ((fromUser = sysIn.readLine()) != null) {
          InetAddress address = InetAddress.getByName(fromUser.trim());
          //modify
          printTable();
          System.out.println("Enter Item ID from the list above ");
          String itemId = sysIn.readLine();
          boolean isIdValid = itemId.equals("00001") || 
                              itemId.equals("00002") || 
                              itemId.equals("00003") || 
                              itemId.equals("00004") || 
                              itemId.equals("00005") || 
                              itemId.equals("00006");
          if (!isIdValid) {
                System.out.println("Invalid Item ID. Please enter a valid Item ID from the list.");
                continue;
            }
          long sendTime = System.currentTimeMillis();
			 
          // send request
          
		  byte[] buf = itemId.getBytes();
          DatagramPacket udpPacket = new DatagramPacket(buf, buf.length, address, 5340); //set port to 5340 
          udpSocket.send(udpPacket);
    
          // get response
          byte[] buf2 = new byte[256];
          DatagramPacket udpPacket2 = new DatagramPacket(buf2, buf2.length);
          udpSocket.receive(udpPacket2);

          long receiveTime = System.currentTimeMillis();
          long rtt = receiveTime - sendTime;

          //send response time to server
          fromServer = new String (udpPacket2.getData(), 0, udpPacket2.getLength());
          String[] responseParts = fromServer.split("\\|");

          System.out.println("\nItem ID\tItem Description\tUnit Price\tInventory\tRTT of Query");
          System.out.println("-------\t----------------\t----------\t---------\t------------");
          if(responseParts.length >= 4) {
              System.out.println(responseParts[0] + "\t" + responseParts[1] + "\t\t" + responseParts[2] + "\t\t" + responseParts[3] + "\t\t" + rtt);
          }
          
		  System.out.println("Do you want to continue? (yes/no)");
          String continueInput = sysIn.readLine();
          if (!continueInput.equalsIgnoreCase("yes")) {
              break;
          }
          System.out.println("Enter DNS or IP Address: ");
        }	 
          
        udpSocket.close();
        System.out.println("Client closed.");
    }
}
