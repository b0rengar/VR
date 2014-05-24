package messages;

import manager.SceneManager;
import com.jme3.network.serializing.Serializable;

@Serializable()
public class EntityActionMessage extends SyncMessage{
    private long entity_id;
    private int action;

    public final static int PLAYER_INTERACT = 0;
    public final static int TRANSFORM = 1;
    
    public EntityActionMessage() { }

    public EntityActionMessage(long entity_id, int action) {
        this.setSyncId(-1);
        this.entity_id = entity_id;
        this.action=action;
    }

    public int getAction() {
		return action;
	}

	@Override
    public void applyData(Object object) {
        SceneManager sceneManager = (SceneManager) object;
        sceneManager.doAction(entity_id, action);
    }

	public long getEntity_id() {
		return entity_id;
	}
	
	

}
