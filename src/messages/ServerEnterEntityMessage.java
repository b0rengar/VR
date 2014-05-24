package messages;

import manager.SceneManager;

import com.jme3.network.serializing.Serializable;

@Serializable()
public class ServerEnterEntityMessage extends SyncMessage{

    private long player_id;
    private long entity_id;

    public ServerEnterEntityMessage() { }

    public ServerEnterEntityMessage(long player_id, long entity_id) {
        setSyncId(-1);
        this.player_id = player_id;
        this.entity_id = entity_id;
    }
    
    @Override
    public void applyData(Object object) {
        SceneManager manager = (SceneManager) object;
        manager.enterEntity(player_id, entity_id);
    }
    
    
}
