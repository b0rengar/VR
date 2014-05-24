package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * Abstract Sync-Nachricht
 * 
 */
@Serializable()
public abstract class SyncMessage extends AbstractMessage {

    protected long sync_id = -1;
    protected double time;

    public SyncMessage() {
        super(true);
    }
    
    public abstract void applyData(Object object);

	public long getSyncId() {
		return sync_id;
	}

	public void setSyncId(long sync_id) {
		this.sync_id = sync_id;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}
}
