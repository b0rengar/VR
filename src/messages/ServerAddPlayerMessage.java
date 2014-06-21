package messages;

import manager.SceneManager;
import com.jme3.network.serializing.Serializable;

@Serializable()
public class ServerAddPlayerMessage extends SyncMessage{
    private long playerId;
    private String name;
    private int client_id;
    private int O2;

    public ServerAddPlayerMessage() { }

    public ServerAddPlayerMessage(long id, String name, int client_id, int O2) {
        this.setSyncId(-1);
        this.playerId = id;
        this.name = name;
        this.client_id = client_id;
        this.O2 = O2;
    }

    @Override
    public void applyData(Object object) {
        SceneManager worldManager = (SceneManager) object;
        worldManager.addPlayer(this.playerId, this.client_id, this.name, this.O2);
    }

}
