package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * Protokolversionspr�fung
 * 
 */
@Serializable()
public class HandshakeMessage extends AbstractMessage {

    private int protocol_version;

    public HandshakeMessage() {}

    public HandshakeMessage(int protocol_version) {
        this.protocol_version=protocol_version;
    }

	public int getProtocol_version() {
		return protocol_version;
	}

}
