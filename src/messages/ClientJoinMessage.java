package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import listener.ClientNetListener;

@Serializable()
public class ClientJoinMessage extends AbstractMessage{
    private String name;
    private String pass;
    private int O2;

    public ClientJoinMessage() { }

    public ClientJoinMessage(String name, String pass, int O2) {
        this.name = name;
        this.pass = pass;
        this.O2 = O2;
        Logger.getLogger(ClientNetListener.class.getName()).log(Level.INFO, "ClientJoinMessage");

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

        public int getO2() {
            return O2;
        }

        public void setO2(int O2) {
            this.O2 = O2;
        }
    
}
