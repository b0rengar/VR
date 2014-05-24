package controls;

import com.jme3.scene.control.Control;
/**
 * Interface used to unify performing actions for autonomous and manual controls
 * @author normenhansen
 */
public interface NetworkActionEnabled extends Control{
    public void doPerformAction(int action, boolean activate);
}
