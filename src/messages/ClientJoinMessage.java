package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable()
public class ClientJoinMessage extends AbstractMessage{
    private String name;
    private String pass;

    public ClientJoinMessage() { }

    public ClientJoinMessage(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
    
    
}
