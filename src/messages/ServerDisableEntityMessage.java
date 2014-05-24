package messages;

import manager.SceneManager;

import com.jme3.network.serializing.Serializable;

@Serializable()
public class ServerDisableEntityMessage extends SyncMessage {

    private long entityId;

    public ServerDisableEntityMessage() {}

    public ServerDisableEntityMessage(long id) {
        this.entityId = id;
        this.setSyncId(-1);
    }

    @Override
    public void applyData(Object object) {
        SceneManager manager = (SceneManager) object;
        manager.disableEntity(entityId);
    }
}
