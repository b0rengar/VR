package messages;

import controls.NetworkActionEnabled;

import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Spatial;

@Serializable()
public class ActionMessage extends SyncMessage {

    private int action;
    private boolean pressed;
	
    public final static int NULL_ACTION = 0;
    public final static int JUMP_ACTION = 1;
    public final static int ENTER_ACTION = 2;
    public final static int LEFT_CLICK_ACTION = 3;
    public final static int RIGHT_CLICK_ACTION = 4;
    public final static int PLAYER_INTERACT = 5;

    public ActionMessage() { }

    public ActionMessage(long id, int action, boolean pressed) {
        this.setSyncId(id);
        this.action = action;
        this.pressed = pressed;
    }

    @Override
    public void applyData(Object object) {
        ((Spatial)object).getControl(NetworkActionEnabled.class).doPerformAction(action, pressed);
    }

	public int getAction() {
		return action;
	}

	public boolean isPressed() {
		return pressed;
	}
}
