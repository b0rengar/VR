/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import event.FireAlarmSystemEventTypes;

/**
 *
 * @author Admin
 */
@Serializable
public class SensorChangeMessage extends AbstractMessage{
    
    private int sensorGroupID;
    
    private int sensorID;
    
    private FireAlarmSystemEventTypes status;
    
    private double fireSeverity;

    public SensorChangeMessage(){};
    
    public SensorChangeMessage(int sensorGroupID, int sensorID, FireAlarmSystemEventTypes status, double fireSeverity) {
        this.sensorGroupID = sensorGroupID;
        this.sensorID = sensorID;
        this.status = status;
        this.fireSeverity = fireSeverity;
    }

    public int getSensorGroupID() {
        return sensorGroupID;
    }

    public void setSensorGroupID(int sensorGroupID) {
        this.sensorGroupID = sensorGroupID;
    }

    public int getSensorID() {
        return sensorID;
    }

    public void setSensorID(int sensorID) {
        this.sensorID = sensorID;
    }

    public FireAlarmSystemEventTypes getStatus() {
        return status;
    }

    public void setStatus(FireAlarmSystemEventTypes status) {
        this.status = status;
    }

    public double getFireSeverity() {
        return fireSeverity;
    }

    public void setFireSeverity(double fireSeverity) {
        this.fireSeverity = fireSeverity;
    }
    
    
    
}
