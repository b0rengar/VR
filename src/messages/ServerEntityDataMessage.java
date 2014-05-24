package messages;

import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Spatial;

@Serializable()
public class ServerEntityDataMessage extends SyncMessage {

    private String name;
    private byte type;
    private int intData;
    private float floatData;
    private long longData;
    private boolean booleanData;
    private String stringData;

    public ServerEntityDataMessage() { }

    public ServerEntityDataMessage(long id, String name, Object value) {
        this.name = name;
        setSyncId(id);
        type = getObjectType(value);
        switch (type) {
            case 0:
                intData = (Integer) value;
                break;
            case 1:
                floatData = (Float) value;
                break;
            case 2:
                booleanData = (Boolean) value;
                break;
            case 3:
                stringData = (String) value;
                break;
            case 4:
                longData = (Long) value;
                break;
            default:
                throw new UnsupportedOperationException("Cannot apply wrong userdata type.");
        }
    }

    @Override
    public void applyData(Object object) {
        Spatial spat = ((Spatial) object);
        switch (type) {
            case 0:
                spat.setUserData(name, intData);
                break;
            case 1:
                spat.setUserData(name, floatData);
                break;
            case 2:
                spat.setUserData(name, booleanData);
                break;
            case 3:
                spat.setUserData(name, stringData);
                break;
            case 4:
                spat.setUserData(name, longData);
                break;
            default:
                throw new UnsupportedOperationException("Cannot apply wrong userdata type.");
        }
    }

    private static byte getObjectType(Object type) {
        if (type instanceof Integer) {
            return 0;
        } else if (type instanceof Float) {
            return 1;
        } else if (type instanceof Boolean) {
            return 2;
        } else if (type instanceof String) {
            return 3;
        } else if (type instanceof Long) {
            return 4;
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type.getClass().getName());
        }
    }
}
