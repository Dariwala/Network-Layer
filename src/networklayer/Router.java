/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networklayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author samsung
 */
public class Router implements Serializable{
    private int routerId;
    private int numberOfInterfaces;
    private ArrayList<IPAddress> interfaceAddrs;//list of IP address of all interfaces of the router
    private ArrayList<RoutingTableEntry> routingTable;//used to implement DVR
    private ArrayList<Integer> neighborRouterIds;//Contains both "UP" and "DOWN" state routers
    private Boolean state;//true represents "UP" state and false is for "DOWN" state

    public Router() {
        interfaceAddrs = new ArrayList<>();
        routingTable = new ArrayList<>();
        neighborRouterIds = new ArrayList<>();

        /**
         * 80% Probability that the router is up
         */
        Random random = new Random();
        double p = random.nextDouble();
        if(p<=0.80) state = true;
        else state = false;

        numberOfInterfaces = 0;
    }

    public Router(int routerId, ArrayList<Integer> neighborRouters, ArrayList<IPAddress> interfaceAddrs)
    {
        this.routerId = routerId;
        this.interfaceAddrs = interfaceAddrs;
        this.neighborRouterIds = neighborRouters;
        routingTable = new ArrayList<>();

        /**
         * 80% Probability that the router is up
         */
        Random random = new Random();
        double p = random.nextDouble();
        if(p<=0.80) state = true;
        else state = false;

        numberOfInterfaces = this.interfaceAddrs.size();
    }

    @Override
    public String toString() {
        String temp = "";
        temp+="Router ID: "+routerId+"\n";
        temp+="Intefaces: \n";
        for(int i=0;i<numberOfInterfaces;i++)
        {
            temp+=interfaceAddrs.get(i).getString()+"\t";
        }
        temp+="\n";
        temp+="Neighbors: \n";
        for(int i=0;i<neighborRouterIds.size();i++)
        {
            temp+=neighborRouterIds.get(i)+"\t";
        }
        return temp;
    }



    /**
     * Initialize the distance(hop count) for each router.
     * for itself, distance=0; for any connected router with state=true, distance=1; otherwise distance=Constants.INFTY;
     */
    public void initiateRoutingTable()
    {
        routingTable.clear();
        for(Router router : NetworkLayerServer.routers){

            int routerId = router.getRouterId();
            if(routerId == this.routerId){
                routingTable.add(new RoutingTableEntry(routerId,0,routerId));
            }
            else if(neighborRouterIds.contains(routerId)){
                if(router.getState()){
                    routingTable.add(new RoutingTableEntry(routerId,1,routerId));
                }
                else {
                    routingTable.add(new RoutingTableEntry(routerId,Constants.INFTY,-1));
                }
            }
            else{
                routingTable.add(new RoutingTableEntry(routerId,Constants.INFTY,-1));
            }

        }
    }

    /**
     * Delete all the routingTableEntry
     */
    public void clearRoutingTable()
    {
        routingTable.clear();
        //System.out.println(routingTable.size() + " hehe");
    }

    /**
     * Update the routing table for this router using the entries of Router neighbor
     * @param neighbor
     */
    public boolean updateRoutingTableSimple(Router neighbor)
    {
        boolean isChanged = false;
        int index = 0;

        for(RoutingTableEntry routingTableEntry : neighbor.getRoutingTable()){
            int distance = routingTableEntry.getDistance();
            int gatewayRouterId = routingTableEntry.getGatewayRouterId();
            //System.out.println(neighbor.getRoutingTable().size());
            RoutingTableEntry ownRoutingTableEntry = routingTable.get(index);
            int ownDistance = ownRoutingTableEntry.getDistance();
            int ownGatewayRouterId = ownRoutingTableEntry.getGatewayRouterId();


            if(distance + 1 < ownDistance){
                ownRoutingTableEntry.setDistance(distance + 1);
                ownRoutingTableEntry.setGatewayRouterId(neighbor.getRouterId());
                isChanged = true;
            }

            index++;
        }
        return isChanged;
        // if((ownGatewayRouterId == neighbor.routerId) || ((distance + 1 < ownDistance) && (this.routerId != gatewayRouterId)))
    }

    public boolean updateRoutingTable(Router neighbor)
    {
        boolean isChanged = false;
        int index = 0;

        for(RoutingTableEntry routingTableEntry : neighbor.getRoutingTable()){
            int distance = routingTableEntry.getDistance();
            int gatewayRouterId = routingTableEntry.getGatewayRouterId();
            //System.out.println(neighbor.getRoutingTable().size() + " dhur");
            RoutingTableEntry ownRoutingTableEntry = routingTable.get(index);
            int ownDistance = ownRoutingTableEntry.getDistance();
            int ownGatewayRouterId = ownRoutingTableEntry.getGatewayRouterId();


            if((ownGatewayRouterId == neighbor.routerId) || ((distance + 1 < ownDistance) && (this.routerId != gatewayRouterId))){
                if(distance + 1 <= Constants.INFTY && distance + 1 !=ownDistance){
                    ownRoutingTableEntry.setDistance(distance + 1);
                    ownRoutingTableEntry.setGatewayRouterId(neighbor.getRouterId());
                    isChanged = true;
                }
            }

            index++;
        }
        return isChanged;
        // if((ownGatewayRouterId == neighbor.routerId) || ((distance + 1 < ownDistance) && (this.routerId != gatewayRouterId)))
    }

    /**
     * If the state was up, down it; if state was down, up it
     */
    public void revertState()
    {
        state=!state;
        if(state==true) this.initiateRoutingTable();
        else this.clearRoutingTable();
    }

    public int getRouterId() {
        return routerId;
    }

    public void setRouterId(int routerId) {
        this.routerId = routerId;
    }

    public int getNumberOfInterfaces() {
        return numberOfInterfaces;
    }

    public void setNumberOfInterfaces(int numberOfInterfaces) {
        this.numberOfInterfaces = numberOfInterfaces;
    }

    public ArrayList<IPAddress> getInterfaceAddrs() {
        return interfaceAddrs;
    }

    public void setInterfaceAddrs(ArrayList<IPAddress> interfaceAddrs) {
        this.interfaceAddrs = interfaceAddrs;
        numberOfInterfaces = this.interfaceAddrs.size();
    }

    public ArrayList<RoutingTableEntry> getRoutingTable() {
        return routingTable;
    }

    public void addRoutingTableEntry(RoutingTableEntry entry) {
        this.routingTable.add(entry);
    }

    public ArrayList<Integer> getNeighborRouterIds() {
        return neighborRouterIds;
    }

    public void setNeighborRouterIds(ArrayList<Integer> neighborRouterIds) {
        this.neighborRouterIds = neighborRouterIds;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }


}
