/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networklayer;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author samsung
 */
public class Client {
    public static void main(String[] args) throws Exception
    {
        Socket socket;
        NetworkUtil nc = null;

        try {
            socket = new Socket("localhost", 24896);
            nc = new NetworkUtil(socket);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Connected to server");
        /**
         * Tasks
         */
        /**
        1. Receive EndDevice configuration from server
        2. Receive active client list from server
        3. for(int i=0;i<100;i++)
        4. {
        5.      Generate a random message
        6.      Assign a random receiver from active client list
        7.      if(i==20)
        8.      {
        9.            Send the message and recipient IP address to server and a special request "SHOW_ROUTE"
        10.           Display routing path, hop count and routing table of each router [You need to receive
                            all the required info from the server in response to "SHOW_ROUTE" request]
        11.     }
        12.     else
        13.     {
        14.           Simply send the message and recipient IP address to server.
        15.     }
        16.     If server can successfully send the message, client will get an acknowledgement along with hop count
                    Otherwise, client will get a failure message [dropped packet]
        17. }
        18. Report average number of hops and drop rate
        */
        EndDevice endDevice = (EndDevice)nc.read();
        Thread.sleep(12000);
        for(int i=0;i<5;i++){


            String message = "abc";
            EndDevice recipient = (EndDevice)nc.read();
            if(i==2){
                String specialMsg = "SHOW_ROUTE";
                Packet packet = new Packet(message,specialMsg,endDevice.getIp(),recipient.getIp(),endDevice.getGateway(),recipient.getGateway());
                nc.write(packet);
            }
            else{
                String specialMsg = "NOTHING_TO_SHOW";
                Packet packet = new Packet(message,specialMsg,endDevice.getIp(),recipient.getIp(),endDevice.getGateway(),recipient.getGateway());
                nc.write(packet);
            }
            Packet packet = (Packet)nc.read();
            if(packet.getMessage().equals("sent")){
                System.out.println("Successfully sent to receiver");
                System.out.println(packet.getHopCount());
                if(i==2){
                    System.out.println(packet.getWholePath());
                    ArrayList<Router>routers = packet.getRoutere();

                    for(Router router : routers){
                        for(RoutingTableEntry routingTableEntry : router.getRoutingTable()){
                            System.out.println(routingTableEntry.getRouterId() + " " + routingTableEntry.getDistance()
                                    + " " + routingTableEntry.getGatewayRouterId());
                        }
                    }
                }
            }
            else{
                System.out.println("Packet dropped");
                if(i==2){
                    System.out.println(packet.getWholePath() + "(dropped)");
                }
            }
        }
        Thread.sleep(12000);
        nc.closeConnection();

    }
}
