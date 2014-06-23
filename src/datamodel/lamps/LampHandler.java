/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel.lamps;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 *
 * @author Admin
 */
public class LampHandler implements ContentHandler{  
    
    
     public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
         
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
