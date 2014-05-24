package controls;

import config.Settings;
import messages.ActionMessage;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;

/**
 * UserCharacterControl
 *
 */
@SuppressWarnings("deprecation")
public class UserCharacterControl extends NetworkedUserControl {
    private Spatial spatial;
	private CharacterControl characterControl;
    private Vector3f walkDirection = new Vector3f(Vector3f.ZERO);
    private Vector3f viewDirection = new Vector3f(Vector3f.UNIT_Z);
    private Vector3f directionLeft = new Vector3f(Vector3f.UNIT_X);
    private Quaternion directionQuat = new Quaternion();
    private float rotAmountX = 0;
    private float rotAmountY = 0;
    private float walkAmount = 0;
    private float strafeAmount = 0;
    private float speed = Settings.getInstance().getCharacter_speed() * Settings.getInstance().getPhysics_fps();
    private Vector3f temp = new Vector3f();

    public UserCharacterControl() { }

    public UserCharacterControl(Client client, long entityId) {
        super(client, entityId);
    }

    @Override
    public void doSteerX(float amount) {
        rotAmountX = amount;
    }

    @Override
    public void doSteerY(float amount) {
        rotAmountY = amount;
    }

    @Override
    public void doMoveX(float amount) {
        strafeAmount = amount;
    }

    @Override
    public void doMoveY(float amount) {}

    @Override
    public void doMoveZ(float amount) {
        walkAmount = amount;
    }

    @Override
    public void doPerformAction(int button, boolean pressed) {
        if (pressed && button == ActionMessage.JUMP_ACTION) {
            characterControl.jump();
        }
    }

    public Vector3f getAimDirection() {
        return viewDirection;
    }

    public Vector3f getLocation() {
        return characterControl.getPhysicsLocation(temp);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        this.spatial = spatial;
        if (spatial == null) {
            return;
        }
        this.characterControl = spatial.getControl(CharacterControl.class);
        if (this.characterControl == null) {
            throw new IllegalStateException("UserCharacterControl kann ohne UserControl nicht hinzugefuegt werden");
        }
        Float spatialSpeed = (Float) spatial.getUserData("Speed");
        if (spatialSpeed != null) {
            speed = spatialSpeed * Settings.getInstance().getPhysics_fps();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void update(float time_last_update) {
        if (!enabled) return;
        if (!characterControl.getWalkDirection().equals(walkDirection) || !characterControl.getViewDirection().equals(viewDirection)) {
            walkDirection.set(characterControl.getWalkDirection());
            viewDirection.set(characterControl.getViewDirection()).normalizeLocal();
            directionLeft.set(viewDirection).normalizeLocal();
            new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y).multLocal(directionLeft);
        }
        walkDirection.set(viewDirection).multLocal(speed * walkAmount);
        walkDirection.addLocal(directionLeft.mult(speed * strafeAmount));
        if (rotAmountX != 0) {
            directionQuat.fromAngleAxis((FastMath.PI) * time_last_update * rotAmountX, Vector3f.UNIT_Y);
            directionQuat.multLocal(walkDirection);
            directionQuat.multLocal(viewDirection);
            directionQuat.multLocal(directionLeft);
        }
        if (rotAmountY != 0) {
            directionQuat.fromAngleAxis((FastMath.PI) * time_last_update * rotAmountY, directionLeft);
            directionQuat.multLocal(viewDirection);
            if (viewDirection.getY() > 0.3f || viewDirection.getY() < - 0.3f) {
                directionQuat.fromAngleAxis((FastMath.PI) * time_last_update * - rotAmountY, directionLeft);
                directionQuat.multLocal(viewDirection);
            }
        }
        characterControl.setWalkDirection(walkDirection);
        characterControl.setViewDirection(viewDirection);
        spatial.getLocalRotation().lookAt(temp.set(viewDirection).multLocal(1, 0, 1), Vector3f.UNIT_Y);
        spatial.setLocalRotation(spatial.getLocalRotation());
    }

    public void render(RenderManager rm, ViewPort vp) { }
}
