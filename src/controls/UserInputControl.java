package controls;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.io.IOException;

import controls.UserControl;
import messages.ActionMessage;

public class UserInputControl implements Control, ActionListener, AnalogListener {

	private InputManager inputManager;
	private Spatial spatial = null;
	private UserControl manualControl = null;
	private boolean enabled = true;
	private float moveX = 0;
	private float moveZ = 0;
	private float steerX = 0;
	private float steerY = 0;
	private Camera cam;

	public UserInputControl(InputManager inputManager, Camera cam) {
		this.inputManager = inputManager;
		this.cam = cam;
		prepareInputManager();
	}

	private void prepareInputManager() {
		inputManager.addMapping("UserInput_Left_Key", new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping("UserInput_Right_Key", new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping("UserInput_Up_Key", new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping("UserInput_Down_Key", new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping("UserInput_Left_Arrow_Key", new KeyTrigger(KeyInput.KEY_LEFT));
		inputManager.addMapping("UserInput_Right_Arrow_Key", new KeyTrigger(KeyInput.KEY_RIGHT));
		inputManager.addMapping("UserInput_Space_Key", new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addMapping("UserInput_Enter_Key", new KeyTrigger(KeyInput.KEY_RETURN));
		inputManager.addMapping("UserInput_Left_Mouse", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addMapping("UserInput_Right_Mouse", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		inputManager.addMapping("UserInput_Mouse_Axis_X_Left", new MouseAxisTrigger(MouseInput.AXIS_X, true));
		inputManager.addMapping("UserInput_Mouse_Axis_X_Right", new MouseAxisTrigger(MouseInput.AXIS_X, false));
		inputManager.addMapping("UserInput_Mouse_Axis_Y_Up", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		inputManager.addMapping("UserInput_Mouse_Axis_Y_Down", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		inputManager.addListener(this,
				"UserInput_Left_Key",
				"UserInput_Right_Key",
				"UserInput_Up_Key",
				"UserInput_Down_Key",
				"UserInput_Left_Arrow_Key",
				"UserInput_Right_Arrow_Key",
				"UserInput_Space_Key",
				"UserInput_Enter_Key",
				"UserInput_Left_Mouse",
				"UserInput_Right_Mouse",
				"UserInput_Mouse_Axis_X_Left",
				"UserInput_Mouse_Axis_X_Right",
				"UserInput_Mouse_Axis_Y_Up",
				"UserInput_Mouse_Axis_Y_Down");
	}

	public void setSpatial(Spatial spatial) {
		this.spatial = spatial;
		if (spatial == null) {
			manualControl = null;
			return;
		}
		manualControl = spatial.getControl(UserControl.class);
		if (manualControl == null) throw new IllegalStateException("Cannot add UserInputControl to spatial without ManualControl!");
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void update(float tpf) {
		if (steerX != 0)
			steerX = 0;
		else
			manualControl.steerX(steerX);
		if (steerY != 0)
			steerY = 0;
		else
			manualControl.steerY(steerY);

		Vector3f currentUp = spatial.getWorldRotation().mult(Vector3f.UNIT_Y);
		Vector3f camLocation = spatial.getWorldTranslation().add(currentUp);
//		System.out.println(camLocation);
		camLocation.setY(camLocation.y-0.5f);
		//        camLocation.setZ(camLocation.z+5);
		//        camLocation.setX(camLocation.x+5);
		cam.setLocation(camLocation);
		cam.setRotation(spatial.getWorldRotation());
		Vector3f vec = manualControl.getAimDirection();
		cam.lookAt(camLocation.addLocal(vec), spatial.getWorldRotation().mult(Vector3f.UNIT_Y));
	}

	public void render(RenderManager rm, ViewPort vp) { }

	public void onAnalog(String binding, float value, float tpf) {
		if (!isEnabled() || manualControl == null) return;
		if (binding.equals("UserInput_Mouse_Axis_X_Left")) {
			steerX = value / tpf;
			steerX = steerX > 1 ? 1 : steerX;
			manualControl.steerX(steerX);
		} else if (binding.equals("UserInput_Mouse_Axis_X_Right")) {
			steerX = value / tpf;
			steerX = steerX > 1 ? 1 : steerX;
			manualControl.steerX(-steerX);
		} else if (binding.equals("UserInput_Mouse_Axis_Y_Up")) {
			steerY = value / tpf;
			steerY = steerY > 1 ? 1 : steerY;
			manualControl.steerY(steerY);
		} else if (binding.equals("UserInput_Mouse_Axis_Y_Down")) {
			steerY = value / tpf;
			steerY = steerY > 1 ? 1 : steerY;
			manualControl.steerY(-steerY);
		}
	}

	public void onAction(String binding, boolean value, float tpf) {
		if (!isEnabled() || manualControl == null) return;
		if (binding.equals("UserInput_Left_Key")) {
			if (value) {
				moveX += 1;
				manualControl.moveX(moveX);
			} else {
				moveX -= 1;
				manualControl.moveX(moveX);
			}
		} else if (binding.equals("UserInput_Right_Key")) {
			if (value) {
				moveX -= 1;
				manualControl.moveX(moveX);
			} else {
				moveX += 1;
				manualControl.moveX(moveX);
			}
		} else if (binding.equals("UserInput_Up_Key")) {
			if (value) {
				moveZ += 1;
				manualControl.moveZ(moveZ);
			} else {
				moveZ -= 1;
				manualControl.moveZ(moveZ);
			}
		} else if (binding.equals("UserInput_Down_Key")) {
			if (value) {
				moveZ -= 1;
				manualControl.moveZ(moveZ);
			} else {
				moveZ += 1;
				manualControl.moveZ(moveZ);
			}
		} else if (binding.equals("UserInput_Space_Key")) {
			manualControl.performAction(ActionMessage.JUMP_ACTION, value);
		} else if (binding.equals("UserInput_Enter_Key")) {
			manualControl.performAction(ActionMessage.ENTER_ACTION, value);
		} else if (binding.equals("UserInput_Left_Mouse")) {
                        manualControl.performAction(ActionMessage.LEFT_CLICK_ACTION, value);
		} else if (binding.equals("UserInput_Right_Mouse")) {                        
			manualControl.performAction(ActionMessage.RIGHT_CLICK_ACTION, value);
		}
	}

	public Control cloneForSpatial(Spatial spatial) {
		throw new UnsupportedOperationException("Not supported.");
	}

	public void write(JmeExporter ex) throws IOException {
		throw new UnsupportedOperationException("Not supported.");
	}

	public void read(JmeImporter im) throws IOException {
		throw new UnsupportedOperationException("Not supported.");
	}
}
