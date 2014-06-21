package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable()
public class ServerPlayerDataMessage extends AbstractMessage {

    private long id;
    private String name;
    private byte type;
    private int intData;
    private float floatData;
    private long longData;
    private boolean booleanData;
    private String stringData;

    public ServerPlayerDataMessage() {}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
        
	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public int getIntData() {
		return intData;
	}

	public void setIntData(int intData) {
		this.intData = intData;
	}

	public float getFloatData() {
		return floatData;
	}

	public void setFloatData(float floatData) {
		this.floatData = floatData;
	}

	public long getLongData() {
		return longData;
	}

	public void setLongData(long longData) {
		this.longData = longData;
	}

	public boolean isBooleanData() {
		return booleanData;
	}

	public void setBooleanData(boolean booleanData) {
		this.booleanData = booleanData;
	}

	public String getStringData() {
		return stringData;
	}

	public void setStringData(String stringData) {
		this.stringData = stringData;
	}
}
