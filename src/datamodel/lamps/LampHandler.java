/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel.lamps;

import datamodel.sensors.SensorGroup;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 *
 * @author Admin
 */
public class LampHandler implements ContentHandler{  
    
    private LampManager lm;
    
    public LampHandler(LampManager lm){
        this.lm = lm;
    }
    
     public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
          if(qName.equals("lamp")){
            double x = Double.parseDouble(atts.getValue("x"));
            double y = Double.parseDouble(atts.getValue("y"));
            double z = Double.parseDouble(atts.getValue("z"));
            
            lm.addLamp(new Lamp(atts.getValue("name"), x, y, z));
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
    
    public void endElement(String uri, String localName, String qName) throws SAXException {}

    public void characters(char[] ch, int start, int length) throws SAXException {}

}
