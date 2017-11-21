/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networklayer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author samsung
 */
public class Packet implements Serializable{
    private String message;
    private String specialMessage;  //ex: "SHOW_ROUTE" request
    private IPAddress destinationIP;
    private IPAddress sourceIP;
    private IPAddress sourceGateway;
    private IPAddress destinationGateway;
    private int hopCount;
    private String wholePath;
    private ArrayList<Router>routere = new ArrayList<>();

    public Packet(String message, String specialMessage, IPAddress sourceIP, IPAddress destinationIP,IPAddress sourceGateway,IPAddress destinationGateway) {
        this.message = message;
        this.specialMessage = specialMessage;
        this.sourceIP = sourceIP;
        this.destinationIP = destinationIP;
        this.sourceGateway = sourceGateway;
        this.destinationGateway = destinationGateway;
    }

    public IPAddress getSourceIP() {
        return sourceIP;
    }

    public void setSourceIP(IPAddress sourceIP) {
        this.sourceIP = sourceIP;
    }

    public IPAddress getSourceGateway() {
        return sourceGateway;
    }

    public void setSourceGateway(IPAddress sourceGateway) {
        this.sourceGateway = sourceGateway;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSpecialMessage() {
        return specialMessage;
    }

    public void setSpecialMessage(String specialMessage) {
        this.specialMessage = specialMessage;
    }

    public IPAddress getDestinationIP() {
        return destinationIP;
    }

    public void setDestinationIP(IPAddress destinationIP) {
        this.destinationIP = destinationIP;
    }

    public IPAddress getDestinationGateway(){return destinationGateway;}

    public void setDestinationGateway(IPAddress destinationGateway){this.destinationGateway = destinationGateway;}

    public void setHopCount(int hopCount){this.hopCount=hopCount;}

    public int getHopCount(){return hopCount;}

    public void setWholePath(String wholePath){this.wholePath = wholePath;}

    public String getWholePath(){return wholePath;}

    public void setRouters(ArrayList<Router> routere){this.routere = routere;}

    public ArrayList<Router> getRoutere(){return routere;}

}
