package messages;

import manager.SceneManager;
import com.jme3.network.serializing.Serializable;

@Serializable()
public class ServerRemovePlayerMessage extends SyncMessage {

    private long playerId;

    public ServerRemovePlayerMessage() { }

    public ServerRemovePlayerMessage(long id) {
        this.setSyncId(-1);
        this.playerId = id;
    }

    @Override
    public void applyData(Object object) {
        SceneManager manager = (SceneManager) object;
        manager.removePlayer(playerId);
    }
}
