package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable()
public class ServerJoinMessage extends AbstractMessage{
    public boolean rejected;
    public long id;
    public int client_id;
    public String name;
    public int O2;

    public ServerJoinMessage() {}

    public ServerJoinMessage(long id, int client_id, String name, boolean rejected, int O2) {
        this.rejected = rejected;
        this.id = id;
        this.client_id = client_id;
        this.name = name;
        this.O2 = O2;
    }

}
