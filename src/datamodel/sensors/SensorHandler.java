/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel.sensors;


import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
/**
 *
 * @author Admin
 */
public class SensorHandler implements ContentHandler{

    private SensorManager sensorManager;
    private SensorGroup sensorGroup;
    private Sensor sensor;
    
    private boolean inSensor = false;
    
    private StringBuilder innerTextBuilder;
    
    public SensorHandler(SensorManager sm) {
        sensorManager = sm;
        innerTextBuilder = new StringBuilder();
    }

    
    
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
       //System.out.println("start " + qName);
        innerTextBuilder.setLength(0);
        
        if(qName.equals("sensorgroup")){
            int id = Integer.parseInt(atts.getValue("id"));
            sensorGroup = new SensorGroup(id);
            sensorManager.addSensorGroup(id, sensorGroup);
        }
        else if(qName.equals("sensor")){
            inSensor = true;
            int id = Integer.parseInt(atts.getValue("id"));
            sensor = new Sensor(id);
            sensorGroup.addSensor(id, sensor);
            
        }
        else if(qName.equals("coordinates")){
            double x = Double.parseDouble(atts.getValue("x"));
            double y = Double.parseDouble(atts.getValue("y"));
            double z = Double.parseDouble(atts.getValue("z"));
            
            sensor.setX(x);
            sensor.setY(y);
            sensor.setZ(z);
        }
        
            
        
        
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        //System.out.println("char '" + innerTextBuilder.toString() + "'");
        //System.out.println("end " + qName);
        
        if(qName.equals("sensor")){
            inSensor = false;
        }
        else if(qName.equals("description")){
            if(inSensor){
                sensor.setDescription(innerTextBuilder.toString());
            }
            else{
                sensorGroup.setDescription(innerTextBuilder.toString());
            }
        }
        else if(qName.equals("type")){
            sensor.setType(innerTextBuilder.toString());
        }
        else if(qName.equals("floor")){
            sensor.setFloor(innerTextBuilder.toString());
        }
        else if(qName.equals("room")){
            sensor.setRoom(innerTextBuilder.toString());
        }
        else if(qName.equals("positiondescription")){
            sensor.setPositionDescription(innerTextBuilder.toString());
        }
        
        
        
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        for(int i = 0; i < length; i++){
            innerTextBuilder.append(ch[start+i]);
        }
       
    }
    
    
    
    /**
     * 
     * Not needed functions
     * 
     */ 
    

    public void startPrefixMapping(String prefix, String uri) throws SAXException {}

    public void endPrefixMapping(String prefix) throws SAXException {}

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}

    public void processingInstruction(String target, String data) throws SAXException {}

    public void skippedEntity(String name) throws SAXException {}
    
    public void setDocumentLocator(Locator locator) {}

    public void startDocument() throws SAXException {}

    public void endDocument() throws SAXException {}
    
}
