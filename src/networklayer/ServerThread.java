/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networklayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author samsung
 */
public class ServerThread implements Runnable {
    private Thread t;
    NetworkUtil nc;
    EndDevice endDevice;
    private int hop_count,source,dest;
    private String wholePath;
    private ArrayList<Router>routers = new ArrayList<>();

    public ServerThread(NetworkUtil nc,EndDevice endDevice){
        this.endDevice = endDevice;
        this.nc = nc;
        //System.out.println("Server Ready for client "+NetworkLayerServer.clientCount);
        NetworkLayerServer.clientCount++;

        t=new Thread(this);
        t.start();
    }

    @Override
    public void run(){
        /**
         * Synchronize actions with client.
         */
        /*
        Tasks:
        1. Upon receiving a packet and recipient, call deliverPacket(packet)
        2. If the packet contains "SHOW_ROUTE" request, then fetch the required information
                and send back to client
        3. Either send acknowledgement with number of hops or send failure message back to client
        */
        for(int i=0;i<100;i++){
            nc.write(NetworkLayerServer.getRandomEndDevice());
            Packet p = (Packet)nc.read();
            boolean sent = deliverPacket(p);
            Packet packet = new Packet("","",null,null,null,null);
            if(sent){
                packet.setDest(dest);
                packet.setSource(source);
                packet.setWholePath(wholePath);
                packet.setMessage("sent");
                packet.setHopCount(hop_count);
                if(p.getSpecialMessage().equals("SHOW_ROUTE")){

                    packet.setRouters(routers);
                }
            }
            else
            {
                packet.setMessage("not sent");
                packet.setWholePath(wholePath);
                packet.setDest(dest);
                packet.setSource(source);
            }
            nc.write(packet);
        }
        try{
            Thread.sleep(12000);
        }
        catch (Exception e){

        }
        NetworkLayerServer.removeEndDevice(endDevice);
        nc.closeConnection();
    }

    /**
     * Returns true if successfully delivered
     * Returns false if packet is dropped
     * @param p
     * @return
     */
    public Boolean deliverPacket(Packet p)
    {
        /*
        1. Find the router s which has an interface
                such that the interface and source end device have same network address.
        2. Find the router d which has an interface
                such that the interface and destination end device have same network address.
        3. Implement forwarding, i.e., s forwards to its gateway router x considering d as the destination.
                similarly, x forwards to the next gateway router y considering d as the destination,
                and eventually the packet reaches to destination router d.

            3(a) If, while forwarding, any gateway x, found from routingTable of router r is in down state[x.state==FALSE]
                    (i) Drop packet
                    (ii) Update the entry with distance Constants.INFTY
                    (iii) Block NetworkLayerServer.stateChanger.t
                    (iv) Apply DVR starting from router r.
                    (v) Resume NetworkLayerServer.stateChanger.t

            3(b) If, while forwarding, a router x receives the packet from router y,
                    but routingTableEntry shows Constants.INFTY distance from x to y,
                    (i) Update the entry with distance 1
                    (ii) Block NetworkLayerServer.stateChanger.t
                    (iii) Apply DVR starting from router x.
                    (iv) Resume NetworkLayerServer.stateChanger.t

        4. If 3(a) occurs at any stage, packet will be dropped,
            otherwise successfully sent to the destination router
        */
        hop_count = 0;
        wholePath = "";
        routers.clear();
       //System.out.println(p.getSourceGateway());
        Router sourceRouter = NetworkLayerServer.getRouter(p.getSourceGateway()) ,
                destinationRouter = NetworkLayerServer.getRouter(p.getDestinationGateway());
        source = sourceRouter.getRouterId();
        dest = destinationRouter.getRouterId();
        //System.out.println(sourceRouter.getRouterId() + " " + destinationRouter.getRouterId());
        Router intermediateRouter = sourceRouter;
        //System.out.println(sourceRouter.getRouterId());
        wholePath = Integer.toString(sourceRouter.getRouterId());
        routers.add(sourceRouter);
        int i = 0;
        while(destinationRouter != intermediateRouter){
            //System.out.println("lol");
            //System.out.println("kochu");
            i++;
            if(i==Constants.INFTY)return false;

            //if(destinationRouter.getState() == false)return false;
            if(intermediateRouter.getState() == false) return false;
            RoutingTableEntry routingTableEntry = intermediateRouter.getRoutingTable().get(destinationRouter.getRouterId()-1);
            //System.out.println("kochu " + routingTableEntry.getGatewayRouterId());
            if(routingTableEntry.getGatewayRouterId()==-1){
                return false;
            }
            Router possibleIntermediateRouter = NetworkLayerServer.routers.get(routingTableEntry.getGatewayRouterId()-1);
            //System.out.println(routingTableEntry.getGatewayRouterId());
            if(possibleIntermediateRouter.getState()) {
                // poss  x
                // int y
                RoutingTableEntry routingTableEntry1 = possibleIntermediateRouter.getRoutingTable().get(intermediateRouter.getRouterId()-1);
                if(routingTableEntry1.getDistance() == Constants.INFTY){
                    routingTableEntry1.setDistance(1);
                    routingTableEntry1.setGatewayRouterId(intermediateRouter.getRouterId());
                    RouterStateChanger.resume = false;
                    NetworkLayerServer.DVR(possibleIntermediateRouter.getRouterId());
                    RouterStateChanger.resume = true;
                }
                hop_count++;
                intermediateRouter = possibleIntermediateRouter;
                wholePath += "->" + Integer.toString(intermediateRouter.getRouterId());
                routers.add(intermediateRouter);
            }
            else{
                routingTableEntry.setDistance(Constants.INFTY);
                routingTableEntry.setGatewayRouterId(-1);
                RouterStateChanger.resume = false;
                NetworkLayerServer.DVR(intermediateRouter.getRouterId());
                RouterStateChanger.resume = true;
                return false;
            }
        }
        //System.out.println("END\n----------");

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj); //To change body of generated methods, choose Tools | Templates.
    }

}
