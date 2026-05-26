/*
 * Jose Trevizo
 * UDP Inventory Lookup System
 * Java server application using UDP sockets.
 * Client requests product information by item ID and measures RTT
 * Server talks to client program from Server Linux Server to Client Linux Server
 */ 
 
import java.io.*;
import java.net.*;
import java.util.*;

class ItemDescrip{
    String id;
    String description;
    double unitPrice;
    int inventory;

    public ItemDescrip(String id, String description, double unitPrice, int inventory){
        this.id = id;
        this.description = description;
        this.unitPrice = unitPrice;
        this.inventory = inventory;
    }
}

public class UDPServer {
    public static void main(String[] args) throws IOException {
        ArrayList<ItemDescrip> itemList = new ArrayList<>();
        itemList.add(new ItemDescrip("00001", "New Inspiron 15", 379.99, 157));
        itemList.add(new ItemDescrip("00002", "New Inspiron 17", 449.99, 128));
        itemList.add(new ItemDescrip("00003", "New Inspiron 15R", 549.99, 202));
        itemList.add(new ItemDescrip("00004", "New Inspiron 15z Ultrabook", 749.99, 315));
        itemList.add(new ItemDescrip("00005", "XPS 14 Ultrabook", 999.99, 261));
        itemList.add(new ItemDescrip("00006", "New XPS 12 UltrabookXPS", 1199.99, 178));
        DatagramSocket udpServerSocket = new DatagramSocket(5340); //set port to 5340 
        System.out.println("Server is running on port 5340...");
        byte[] buf = new byte[256];

        BufferedReader in = null;
        DatagramPacket udpPacket = null, udpPacket2 = null;
        String fromClient = null, toClient = null;
        boolean morePackets = true;

       
        
        while (true) {
            try {

                // receive UDP packet from client
                udpPacket = new DatagramPacket(buf, buf.length);
                udpServerSocket.receive(udpPacket);

                fromClient = new String(udpPacket.getData(), 0, udpPacket.getLength(), "UTF-8");
                System.out.println("Received request for Item ID: " + fromClient);

                // find the item
                String requestedId = fromClient.trim();
                ItemDescrip foundItem = null;
                for (ItemDescrip it : itemList) {
                if (it.id.equals(requestedId)) {
                    foundItem = it;
                    break;
                 }
                }
                
                String response;
                if (foundItem != null) {
                    response =foundItem.id + "|" + 
                              foundItem.description + "|" + 
                              foundItem.unitPrice + "|" + 
                              foundItem.inventory;
                } else {
                    response = "ERROR Item not found";
                }

                
                                         
                // send the response to the client at "address" and "port"
                InetAddress address = udpPacket.getAddress();
                int port = udpPacket.getPort();
                byte[] buf2 = response.getBytes("UTF-8");
                udpPacket2 = new DatagramPacket(buf2, buf2.length, address, port);
                udpServerSocket.send(udpPacket2);

            } catch (IOException e) {
                System.err.println("IOException: " + e.getMessage());
                e.printStackTrace();
                
            }
        }
  
        

    }
}
