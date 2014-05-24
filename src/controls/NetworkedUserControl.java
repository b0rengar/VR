package controls;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.network.Client;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.io.IOException;

import messages.ActionMessage;
import messages.ControlMessage;
import manager.SyncManager;

public abstract class NetworkedUserControl implements UserControl, NetworkActionEnabled {

    protected boolean enabled = true;
    private Client client;
    private long entity_id;
    
    private float lastSteerX = 0;
    private float lastSteerY = 0;
    private float lastMoveX = 0;
    private float lastMoveY = 0;
    private float lastMoveZ = 0;

    public NetworkedUserControl() {
    }

    public NetworkedUserControl(Client client, long entity_id) {
        this.client = client;
        this.entity_id = entity_id;
    }

    public NetworkedUserControl(SyncManager server, long entity_id) {
        this.entity_id = entity_id;
    }

    public void steerX(float amount) {
        if (client != null && amount != lastSteerX) {
            lastSteerX = amount;
            sendMoveSync();
        }
    }

    public void steerY(float amount) {
        if (client != null && amount != lastSteerY) {
            lastSteerY = amount;
            sendMoveSync();
        }
    }

    public void moveX(float amount) {
        if (client != null && amount != lastMoveX) {
            lastMoveX = amount;
            sendMoveSync();
        }
    }

    public void moveY(float amount) {
        if (client != null && amount != lastMoveY) {
            lastMoveY = amount;
            sendMoveSync();
        }
    }

    public void moveZ(float amount) {
        if (client != null && amount != lastMoveZ) {
            lastMoveZ = amount;
            sendMoveSync();
        }
    }

    public void performAction(int button, boolean pressed) {
        if (client != null) {
            client.send(new ActionMessage(entity_id, button, pressed));
        }
    }

    public abstract void doPerformAction(int button, boolean pressed);

    private void sendMoveSync() {
        client.send(new ControlMessage(entity_id, lastSteerX, lastSteerY, lastMoveX, lastMoveY, lastMoveZ));
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
    
    public abstract void doSteerX(float amount);
    public abstract void doSteerY(float amount);
    public abstract void doMoveX(float amount);
    public abstract void doMoveY(float amount);
    public abstract void doMoveZ(float amount);
}
