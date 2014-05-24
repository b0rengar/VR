package messages;

import manager.SceneManager;

import com.jme3.network.serializing.Serializable;

@Serializable()
public class ServerRemoveEntityMessage extends SyncMessage {

    private long entityId;

    public ServerRemoveEntityMessage() { }

    public ServerRemoveEntityMessage(long id) {
        this.entityId = id;
        this.setSyncId(-1);
    }

    @Override
    public void applyData(Object object) {
        SceneManager manager = (SceneManager) object;
        manager.removeEntity(entityId);
    }
}
