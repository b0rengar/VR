/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel.lamps;

import datamodel.sensors.SensorHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author Admin
 */
public class LampManager {
    
    private static LampManager instance;
    
    private HashSet<Lamp> lamps;
    
    public static LampManager getInstance(){
        if(instance == null)
            instance = new LampManager();
        return instance;
    }

    private LampManager() {
        try {
            load(new File("ress/lamps.xml"));
        } catch (SAXException ex) {
            Logger.getLogger(LampManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LampManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LampManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private void load(File file) throws SAXException, FileNotFoundException, IOException{
        
        lamps = new HashSet<Lamp>();
        
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        
        InputSource is = new InputSource(new FileReader(file));
        
        xmlReader.setContentHandler(new LampHandler());
        
        xmlReader.parse(is);
    }
    
    
    
            
    
    
}
