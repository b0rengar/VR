package messages;

import manager.SceneManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

@Serializable()
public class ServerAddEntityMessage extends SyncMessage {

    private long entityId;
    private String modelIdentifier;
    private Vector3f location;
    private Quaternion rotation;
    private Vector3f scale;

    public ServerAddEntityMessage() {}

    public ServerAddEntityMessage(long id, String modelIdentifier, Vector3f location, Quaternion rotation, Vector3f scale) {
        this.setSyncId(-1);
        this.entityId = id;
        this.modelIdentifier = modelIdentifier;
        this.location = location;
        this.rotation = rotation;
        this.scale = scale;
    }

    public void applyData(Object obj) {
        SceneManager manager = (SceneManager) obj;
        manager.addEntity(entityId, modelIdentifier, location, rotation, scale, false);
    }
}
