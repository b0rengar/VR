package messages;

import manager.SceneManager;
import com.jme3.network.serializing.Serializable;

@Serializable()
public class ServerAddPlayerMessage extends SyncMessage{
    private long playerId;
    private String name;
    private int client_id;

    public ServerAddPlayerMessage() { }

    public ServerAddPlayerMessage(long id, String name, int client_id) {
        this.setSyncId(-1);
        this.playerId = id;
        this.name = name;
        this.client_id = client_id;
    }

    @Override
    public void applyData(Object object) {
        SceneManager worldManager = (SceneManager) object;
        worldManager.addPlayer(this.playerId, this.client_id, this.name);
    }

}
