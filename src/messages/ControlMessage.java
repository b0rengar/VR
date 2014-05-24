package messages;

import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Spatial;

import controls.NetworkedUserControl;

/**
 * Controlmessage
 *
 */
@Serializable()
public class ControlMessage extends SyncMessage {

    private float aimX;
    private float aimY;
    private float moveX;
    private float moveY;
    private float moveZ;

    public ControlMessage() {}

    public ControlMessage(ControlMessage msg) {
        this.setSyncId(msg.getSyncId());
        this.aimX = msg.aimX;
        this.aimY = msg.aimY;
        this.moveX = msg.moveX;
        this.moveY = msg.moveY;
        this.moveZ = msg.moveZ;
    }

    public ControlMessage(long id, float aimX, float aimY, float moveX, float moveY, float moveZ) {
        this.setSyncId(id);
        this.aimX = aimX;
        this.aimY = aimY;
        this.moveX = moveX;
        this.moveY = moveY;
        this.moveZ = moveZ;
    }

    @Override
    public void applyData(Object object) {
        NetworkedUserControl netControl = ((Spatial) object).getControl(NetworkedUserControl.class);
        assert (netControl != null);
        netControl.doMoveX(moveX);
        netControl.doMoveY(moveY);
        netControl.doMoveZ(moveZ);
        netControl.doSteerX(aimX);
        netControl.doSteerY(aimY);
    }
}
