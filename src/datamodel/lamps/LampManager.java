/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel.lamps;

import com.jme3.network.Client;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Server;
import datamodel.sensors.Sensor;
import datamodel.sensors.SensorChangeListener;
import datamodel.sensors.SensorHandler;
import datamodel.sensors.SensorManager;
import event.FireAlarmSystemEventTypes;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import messages.ClientJoinMessage;
import messages.LampChangeMessage;
import messages.SensorChangeMessage;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author Admin
 */
public class LampManager  implements MessageListener{
    
    private static LampManager instance;
    
    private HashSet<Lamp> lamps;
    
    private Server server;
    
    private Client client;
    
    private HashSet<LampChangeListener> listener;
    
    public static LampManager getInstance(){
        
        if (instance == null){
            instance = new LampManager();
        }
        return instance;
    }

    private LampManager() {
        
        listener = new HashSet<LampChangeListener>();
        try {
            load(new File("ress/lamps.xml"));
        } catch (SAXException ex) {
            Logger.getLogger(LampManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LampManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LampManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for(Lamp l : lamps){
            System.out.println(l);
        }
    }
    
    
    private void load(File file) throws SAXException, FileNotFoundException, IOException{
        
        lamps = new HashSet<Lamp>();
        
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        
        InputSource is = new InputSource(new FileReader(file));
        
        xmlReader.setContentHandler(new LampHandler(this));
        
        xmlReader.parse(is);
    }
    
    public void addLamp(Lamp lamp){
        lamps.add(lamp);
    }
    
    public Lamp getLamp(int id){
        for(Lamp l : lamps){
            if(l.getID() == id){
                return l;
            }
        }
        return null;
    }
    
    public List<Lamp> getLamps(){
        return new LinkedList<Lamp>(lamps);
    }
    
    public void addLampChangeListener(LampChangeListener lcl){
        listener.add(lcl);
    }
    
    public void removeLampChangeListener(LampChangeListener lcl){
        listener.remove(lcl);
    }
    
    private void notifyListeners(Lamp l){
        for(LampChangeListener lcl : listener){
            lcl.lampChanged(l);
        }
    }
    
    
    public void lampChanged(Lamp l){
        System.out.println("lamp changed");
        if(server == null && client != null){
//            System.out.println("client send");
            
            client.send(new LampChangeMessage(l));
        }
        else if(server != null && client == null){
//            System.out.println("server broadcast");
            server.broadcast(new LampChangeMessage(l));
            notifyListeners(l);
        }
    }

    
    
    
    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        if(this.server != null){
            this.server.removeMessageListener(this);
        }
        this.server = server;
        this.server.addMessageListener(this, LampChangeMessage.class, ClientJoinMessage.class);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        if(client != null){
            client.removeMessageListener(this);
        }
        this.client = client;
        this.client.addMessageListener(this);
    }
    
    
    
    public void messageReceived(Object source, Message m) {
        //System.out.println("receive");
        //client
        //System.out.println("recieve " + m.getClass());
        if(server == null && client != null){
            //System.out.println("client");
            if(m instanceof LampChangeMessage){
                LampChangeMessage lcm = (LampChangeMessage)m;
                Lamp l = getLamp(lcm.getID());
                
                l.visited =  lcm.isVisited();
                System.out.println("client netlamp:\n" + l);
                System.out.println(l.isVisited());
                notifyListeners(l);
            }
        }
        //server
        else if(server != null && client == null){
            //System.out.println("server");
            if(m instanceof LampChangeMessage){
                LampChangeMessage lcm = (LampChangeMessage)m;
                Lamp l = getLamp(lcm.getID());

                l.visited = lcm.isVisited();
                
                System.out.println("server netlamp:\n" + l);
                server.broadcast(new LampChangeMessage(l));
                notifyListeners(l);
            }
            else if(m instanceof ClientJoinMessage){
                HostedConnection hc = (HostedConnection)source;
                
                for(Lamp l: getLamps()){
                    if(l.isVisited()){
                        System.out.println("send init lamps");
                        hc.send(new LampChangeMessage(l));
                    }
                }
            }
        }
    }
    
    
}
