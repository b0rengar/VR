package messages;


import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Spatial;

/**
 * Sync-Nachricht f�r RigidBody
 *
 */
@Serializable()
public class SyncRigidBodyMessage extends SyncMessage {

	private Vector3f location;
	private Matrix3f rotation;
	private Vector3f linearVelocity;
	private Vector3f angularVelocity;

	public SyncRigidBodyMessage() { }

	public SyncRigidBodyMessage(long id, PhysicsRigidBody body) {
		this.sync_id = id;
		location = body.getPhysicsLocation(new Vector3f());
		rotation = body.getPhysicsRotationMatrix(new Matrix3f());
		linearVelocity = new Vector3f();
		body.getLinearVelocity(linearVelocity);
		angularVelocity = new Vector3f();
		body.getAngularVelocity(angularVelocity);
	}

	public void applyData(Object body) {
		if (body == null) return;
		PhysicsRigidBody rigidBody = ((Spatial) body).getControl(RigidBodyControl.class);
		if (rigidBody == null) rigidBody = ((Spatial) body).getControl(VehicleControl.class);
		rigidBody.setPhysicsLocation(location);
		rigidBody.setPhysicsRotation(rotation);
		rigidBody.setLinearVelocity(linearVelocity);
		rigidBody.setAngularVelocity(angularVelocity);
	}
}
