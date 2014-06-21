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
public class SensorManager {
    
    HashMap<Integer, SensorGroup> sensorGroups;

    public SensorManager() {
        sensorGroups = new HashMap<Integer, SensorGroup>();
    }

    public void addSensorGroup(int id, SensorGroup sGroup){
        sensorGroups.put(id, sGroup);
    }
    
    public void addSensor(int sensorGroupID, int sensorID, Sensor sensor){
        SensorGroup sg = getSensorGroup(sensorGroupID);
        if(sg != null){
            sg.addSensor(sensorID, sensor);
        }
    }
    
    public List<SensorGroup> getSensorGroups(){
        return new LinkedList<SensorGroup>(sensorGroups.values());
    }
    
    public List<Sensor> getSensors(){
        LinkedList<Sensor> list = new LinkedList<Sensor>();
        for(SensorGroup sg : sensorGroups.values()){
            list.addAll(sg.getSensors());
        }
        return list;
    }
    
    public SensorGroup getSensorGroup(int id){
        return sensorGroups.get(id);
    }
    
    public Sensor getSensor(int sensorGroupID, int sensorID){
        Sensor s = null;
        SensorGroup sg = getSensorGroup(sensorGroupID);
        if(sg != null){
            s = sg.getSensor(sensorID);
        }
        return s;
    }
    
}
