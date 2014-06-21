/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel.sensors;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class SensorGroup {
    
    private String id;
    private String description;
    
    private HashMap<Integer,Sensor> sensors;


    public SensorGroup(String id) {
        this.id = id;
        sensors = new HashMap<Integer, Sensor>();
    }

    public SensorGroup(String id, String description) {
        this(id);
        this.description = description;
        
    }

    public List<Sensor> getSensors() {
        return new LinkedList<Sensor>(sensors.values());
    }
    
    public Sensor getSensor(int id){
        return sensors.get(id);
    }

    public void addSensor(int id, Sensor sensor) {
        sensor.setGroup(this);
        this.sensors.put(id, sensor);
    }
    
    /**
     * 
     * Setters & Getters
     * 
     */
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
