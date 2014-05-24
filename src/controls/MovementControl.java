package controls;

import com.jme3.math.Vector3f;
import com.jme3.scene.control.Control;

public interface MovementControl extends Control{
    public Vector3f getLocation();
    public Vector3f getAimDirection();
}
