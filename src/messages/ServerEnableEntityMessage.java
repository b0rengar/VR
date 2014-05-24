package messages;

import manager.SceneManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

@Serializable()
public class ServerEnableEntityMessage extends SyncMessage {

    private long entityId;
    private Vector3f location;
    private Quaternion rotation;

    public ServerEnableEntityMessage() { }

    public ServerEnableEntityMessage(long id, Vector3f location, Quaternion rotation) {
        this.setSyncId(-1);
        this.entityId = id;
        this.location = location;
        this.rotation = rotation;
    }

    public void applyData(Object obj) {
        SceneManager manager = (SceneManager) obj;
        manager.enableEntity(entityId, location, rotation);
    }
}
