package messages;


import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Spatial;

/**
 * Sync-Nachricht f�r Characters
 *
 */
@SuppressWarnings("deprecation")
@Serializable()
public class SyncCharacterMessage extends SyncMessage {

    private Vector3f location = new Vector3f();
    private Vector3f walk_direction = new Vector3f();
    private Vector3f view_direction = new Vector3f();

    public SyncCharacterMessage() {}

    public SyncCharacterMessage(long id, CharacterControl character) {
    	this.sync_id = id;
    	location = character.getPhysicsLocation(location);
        this.walk_direction.set(character.getWalkDirection());
        this.view_direction.set(character.getViewDirection());
    }

    public void applyData(Object character) {
        ((Spatial) character).getControl(CharacterControl.class).setPhysicsLocation(location);
        ((Spatial) character).getControl(CharacterControl.class).setWalkDirection(walk_direction);
        ((Spatial) character).getControl(CharacterControl.class).setViewDirection(view_direction);
    }
}
