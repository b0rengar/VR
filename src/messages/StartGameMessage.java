package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable()
public class StartGameMessage extends AbstractMessage{
    public String scene;
    public String[] models;

    public StartGameMessage() { }

    public StartGameMessage(String scene, String[] models) {
        this.scene = scene;
        this.models = models;
    }
}
