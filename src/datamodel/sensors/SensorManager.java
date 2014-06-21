/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel.sensors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author Admin
 */
public class SensorManager {
    
    HashMap<Integer, SensorGroup> sensorGroups;
    
    private static SensorManager instance;

    
    public static SensorManager getInstance(){
        if (instance == null){
            instance = new SensorManager();
        }
        return instance;
    }
    
    private SensorManager() {
        sensorGroups = new HashMap<Integer, SensorGroup>();
        
        try {
            loadSensors(new File("ress/firesensors.xml"));
        } catch (SAXException ex) {
            Logger.getLogger(SensorManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SensorManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SensorManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public final void loadSensors(File file) throws SAXException, FileNotFoundException, IOException{
        System.out.println(file.exists());
        System.out.println(file.getAbsolutePath());
        System.out.println(file.getTotalSpace());
        
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        
        InputSource is = new InputSource(new FileReader(file));
        
        xmlReader.setContentHandler(new SensorHandler(this));
        
        xmlReader.parse(is);
        
        //System.out.println(this);
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        
        for(SensorGroup s: getSensorGroups()){
            sb.append("\n");
            sb.append("\n");
            sb.append(s);
        }
                
        
        return sb.toString();
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
