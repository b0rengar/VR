/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel.sensors;

import com.jme3.network.Client;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Server;
import event.FireAlarmSystemEvent;
import event.FireAlarmSystemEventTypes;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import messages.ClientJoinMessage;
import messages.SensorChangeMessage;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author Admin
 */
public class SensorManager  implements MessageListener{
    
    HashMap<Integer, SensorGroup> sensorGroups;
    
    private static SensorManager instance;
    
    private Server server;
    
    private Client client;
    
    private HashSet<SensorChangeListener> listener;

    
    public static SensorManager getInstance(){
        //System.out.println("getinstance");
        if (instance == null){
            instance = new SensorManager();
        }
        return instance;
    }
    
    private SensorManager() {
        sensorGroups = new HashMap<Integer, SensorGroup>();
        listener = new HashSet<SensorChangeListener>();
        //System.out.println("construct");
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
//        System.out.println(file.exists());
//        System.out.println(file.getAbsolutePath());
//        System.out.println(file.getTotalSpace());
        
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

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        if(server != null){
            server.removeMessageListener(this);
        }
        this.server = server;
        server.addMessageListener(this, SensorChangeMessage.class, ClientJoinMessage.class);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        if(client != null){
            client.removeMessageListener(this);
        }
        this.client = client;
        client.addMessageListener(this);
    }
    
    
    public void sensorChanged(Sensor s){
        if(server == null && client != null){
//            System.out.println("client send");
            
            client.send(new SensorChangeMessage(s.getGroup().getId(), s.getId(), s.getStatus(), s.getFireSeverity()));
        }
        else if(server != null && client == null){
//            System.out.println("server broadcast");
            server.broadcast(new SensorChangeMessage(s.getGroup().getId(), s.getId(), s.getStatus(), s.getFireSeverity()));
        }
    }
    
    public void alarmEvent(FireAlarmSystemEvent fase){
        int groupID = Integer.parseInt(fase.getGroup());
        int sensorID = Integer.parseInt(fase.getAlarmUnit());
        Sensor s = getSensor(groupID, sensorID);
        
        System.out.println("server sensor:\n" + s);
        
        for(FireAlarmSystemEventTypes t : FireAlarmSystemEventTypes.values()){
            if(t.getName().equals(fase.getType())){
                s.setStatus(t);
                if(t.equals(FireAlarmSystemEventTypes.ALARM)){
                    s.setFireSeverity(Math.random()*100 + 1);
                }
            }
        }       
    }

    
    public void messageReceived(Object source, Message m) {
        //System.out.println("receive");
        //client
        if(server == null && client != null){
            if(m instanceof SensorChangeMessage){
                SensorChangeMessage scm = (SensorChangeMessage)m;
                Sensor s = getSensor(scm.getSensorGroupID(), scm.getSensorID());
                s.fireSeverity = scm.getFireSeverity();
                s.status =  scm.getStatus();
                System.out.println("client netsensor:\n" + s);
                System.out.println(s.getStatus().toString());
                notifyListeners(s);
            }
        }
        //server
        else if(server != null && client == null){
            if(m instanceof SensorChangeMessage){
                SensorChangeMessage scm = (SensorChangeMessage)m;
                Sensor s = getSensor(scm.getSensorGroupID(), scm.getSensorID());
                s.fireSeverity = scm.getFireSeverity();
                s.status =  scm.getStatus();
                System.out.println("server netsensor:\n" + s);
                server.broadcast(m);
            }
            else if(m instanceof ClientJoinMessage){
                HostedConnection hc = (HostedConnection)source;
                
                for(Sensor s: getSensors()){
                    System.out.println("loop sensors");
                    if(s.getStatus() != FireAlarmSystemEventTypes.READY){
                        System.out.println("send init sensors");
                        hc.send(new SensorChangeMessage(s.getGroup().getId(), s.getId(), s.getStatus(), s.getFireSeverity()));
                    }
                }
            }
        }
    }
    
    
    public void addSensorChangeListener(SensorChangeListener scl){
        listener.add(scl);
    }
    
    public void removeSensorChangeListener(SensorChangeListener scl){
        listener.remove(scl);
    }
    
    private void notifyListeners(Sensor s){
        for(SensorChangeListener scl : listener){
            scl.sensorChanged(s);
        }
    }
    
}
