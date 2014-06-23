/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import datamodel.lamps.Lamp;
import event.FireAlarmSystemEventTypes;

/**
 *
 * @author Admin
 */
@Serializable
public class LampChangeMessage extends AbstractMessage{
    
    private String name;

    private boolean visited;

    public LampChangeMessage(){}
    
    public LampChangeMessage(Lamp l) {
        this.setName(l.getName());
        this.setVisited(l.isVisited());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }


    
    
    
}
