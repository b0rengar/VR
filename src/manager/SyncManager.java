package manager;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.network.Server;
import com.jme3.network.Client;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.scene.Spatial;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import messages.SyncMessage;
import messages.SyncCharacterMessage;
import messages.SyncRigidBodyMessage;


/**
 * Synchronisationsmanager
 * 
 */
@SuppressWarnings({ "deprecation", "rawtypes" })
public class SyncManager extends AbstractAppState implements MessageListener {
    private HashMap<Long, Object> sync_objects = new HashMap<Long, Object>();
    private Server server;
    private Client client;
    private float sync_frequency = 0.5f;
    private double maxDelay = 0.5f;
    private double time = 0;
    private double offset = Double.MIN_VALUE;
    private float syncTimer = 0;
    private List<SyncMessage> message_queue = new LinkedList<SyncMessage>();
    private Application app;

    public SyncManager(Application app, Server server) {
        this.app = app;
        this.server = server;
    }

    public SyncManager(Application app, Client client) {
        this.app = app;
        this.client = client;
    }

    @Override
    public void update(float time_last_update) {
        time += time_last_update;
        if (time < 0) time = 0; //wegen Ueberlauf Zahlenbereich
        if (client != null) {
            for (Iterator<SyncMessage> it = message_queue.iterator(); it.hasNext();) {
                SyncMessage message = it.next();
                if (message.getTime() >= time + offset) {
                    doMessage(message);
                    it.remove();
                }
            }
        } else if (server != null) {
            syncTimer += time_last_update;
            if (syncTimer >= sync_frequency) {
                sendSyncData();
                syncTimer = 0;
            }
        }
    }

  
    public void addObject(long id, Object object) {
        sync_objects.put(id, object);
    }


    public void removeObject(Object object) {
    	for(Map.Entry<Long, Object> e : sync_objects.entrySet()){
            if (e.getValue() == object) {
            	sync_objects.remove(e.getKey());
                return;
            }
        }
    }

    public void removeObject(long id) {
        sync_objects.remove(id);
    }

    protected void doMessage(SyncMessage message) {
        Object object = sync_objects.get(message.getSyncId());
        if (object != null) message.applyData(object);
    }

    protected void enqueueMessage(SyncMessage message) {
        if (offset == Double.MIN_VALUE) offset = this.time - message.getTime();
        double delayTime = (message.getTime() + offset) - time;
        if (delayTime > maxDelay) offset -= delayTime - maxDelay;
        else if (delayTime < 0) offset -= delayTime;
        message_queue.add(message);
    }


    protected void sendSyncData() {
        for (Iterator<Entry<Long, Object>> it = sync_objects.entrySet().iterator(); it.hasNext();) {
            Entry<Long, Object> entry = it.next();
            if (entry.getValue() instanceof Spatial) {
                Spatial spat = (Spatial) entry.getValue();
                PhysicsRigidBody body = spat.getControl(RigidBodyControl.class);
                if (body == null)
                    body = spat.getControl(VehicleControl.class);
                if (body != null && body.isActive()) {
                    SyncRigidBodyMessage msg = new SyncRigidBodyMessage(entry.getKey(), body);
                    broadcast(msg);
                    continue;
                }
                CharacterControl control = spat.getControl(CharacterControl.class);
                if (control != null)
                    broadcast(new SyncCharacterMessage(entry.getKey(), control));
            }
        }
    }

    public void send(int client_id, SyncMessage msg) {
        if (server == null) {
            Logger.getLogger(SyncManager.class.getName()).log(Level.SEVERE, "Broadcasting message on client "+ msg);
            return;
        } 
        HostedConnection client = server.getConnection(client_id);
        msg.setTime(time);
        if (client == null) {
            Logger.getLogger(SyncManager.class.getName()).log(Level.SEVERE, "Client null when sending: {0}", client);
            return;
        }
        client.send(msg);
        
    }

    @SuppressWarnings("unchecked")
	public void setMessageTypes(Class<?>... classes) {
        if (server != null) {
            server.removeMessageListener(this);
            server.addMessageListener(this, classes);
        } else if (client != null) {
            client.removeMessageListener(this);
            client.addMessageListener(this, classes);
        }
    }

    public void messageReceived(Object source, final Message message) {
        assert (message.getClass() == SyncMessage.class);
        if (client != null) {
            app.enqueue(new Callable<Void>() {
                public Void call() throws Exception {
                    enqueueMessage((SyncMessage) message);
                    return null;
                }
            });
        } else if (server != null) {
            app.enqueue(new Callable<Void>() {
                public Void call() throws Exception {
                    broadcast((SyncMessage) message);
                    doMessage((SyncMessage) message);
                    return null;
                }
            });
        }
    }
    
    public void broadcast(SyncMessage message) {
        if (server == null) return;
        message.setTime(time);
        server.broadcast(message);
    }

    public Server getServer() {
        return server;
    }

    public Client getClient() {
        return client;
    }

    public void setMaxDelay(double maxDelay) {
        this.maxDelay = maxDelay;
    }

    public void setSyncFrequency(float syncFrequency) {
        this.sync_frequency = syncFrequency;
    }
}
