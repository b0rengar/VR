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
    
    private int id;
    private String description;
    
    private HashMap<Integer,Sensor> sensors;


    public SensorGroup(int id) {
        this.id = id;
        sensors = new HashMap<Integer, Sensor>();
    }

    public SensorGroup(int id, String description) {
        this(id);
        this.description = description;
        
    }

    
    @Override
    public String toString(){
        return getDescription();
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
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
