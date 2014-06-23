/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.soap.Addressing;
import listener.ClientNetListener;
import com.jme3.math.Vector3f;

/**
 * 
 * @author Fabian
 */
@Serializable()
public class ClientUserDataMessage extends AbstractMessage{
    private int clientID;
    private String playerName;
    private int O2;
    private int pulse;
    private Vector3f location;

    public ClientUserDataMessage() { }

    public ClientUserDataMessage(String playerName, int O2, int pulse, Vector3f loc) {
        this.playerName = playerName;
        this.O2 = O2;
        this.pulse = pulse;
        this.location = loc;
        Logger.getLogger(ClientNetListener.class.getName()).log(Level.INFO, "ServerUserDataMessage");
    }
    
    public ClientUserDataMessage(int clientID, String playerName, int O2, int pulse, Vector3f loc) {
        this.clientID = clientID;
        this.playerName = playerName;
        this.O2 = O2;
        this.pulse = pulse;
        this.location = loc;
        Logger.getLogger(ClientNetListener.class.getName()).log(Level.INFO, "ClientUserDataMessage");
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getO2() {
        return O2;
    }

    public void setO2(int O2) {
        this.O2 = O2;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public Vector3f getLocation() {
        return location;
    }

    public void setLocation(Vector3f location) {
        this.location = location;
    }
    
}
