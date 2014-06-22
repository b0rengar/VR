package listener;

import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.MessageListener;
import com.jme3.network.Message;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.ClientMain;
import config.Settings;
import manager.SceneManager;
import messages.ClientJoinMessage;
import messages.EntityActionMessage;
import messages.HandshakeMessage;
import messages.ServerAddPlayerMessage;
import messages.ServerJoinMessage;
import messages.ServerRemovePlayerMessage;
import messages.StartGameMessage;
import messages.ClientUserDataMessage;

/**
 * Listener Netzwerknachrichten f�r den Client
 *
 */
@SuppressWarnings("rawtypes")
public class ClientNetListener implements MessageListener, ClientStateListener {

	private ClientMain app;
	private Client client;
	private String name = "";
	private String pass = "";
        private int O2 = 0;
        private int pulse = 140;
	private SceneManager worldManager;
	private boolean worldIsLoaded = false;

	@SuppressWarnings("unchecked")
	public ClientNetListener(ClientMain app, Client client, SceneManager worldManager) {
		this.app = app;
		this.client = client;
		this.worldManager = worldManager;
		client.addClientStateListener(this);
		client.addMessageListener(this, 
				HandshakeMessage.class,
				ServerJoinMessage.class,
				StartGameMessage.class,
				ServerAddPlayerMessage.class,
				ServerRemovePlayerMessage.class,
				EntityActionMessage.class,
                                ClientUserDataMessage.class
				);
	}

	public void clientConnected(Client client) {
		app.setStatusText("Login anfordern..");
		client.send(new HandshakeMessage(Settings.getInstance().getProtocol_version()));
		Logger.getLogger(ClientNetListener.class.getName()).log(Level.INFO, "Sende Handshake-Nachricht");
	}

	public void clientDisconnected(Client clienst, DisconnectInfo info) {
		app.setStatusText("Verbindung mit dem Server fehlgeschlagen!");
	}

	public void messageReceived(Object source, Message message) {
		if (message.getClass() == HandshakeMessage.class) {
			HandshakeMessage msg = (HandshakeMessage) message;
			Logger.getLogger(ClientNetListener.class.getName()).log(Level.INFO, "Handshake kommt zurueck");
			if (msg.getProtocol_version() != Settings.getInstance().getProtocol_version()) {
				app.setStatusText("Falsche Protokoll-Version - Bitte aktualsieren Sie das Programm!");
				Logger.getLogger(ClientNetListener.class.getName()).log(Level.INFO, "Falsche Protokoll-Version, Verbindung wird getrennt");
				return;
			}
			client.send(new ClientJoinMessage(this.name, this.pass, this.O2));
		} else if (message.getClass() == ServerJoinMessage.class) {
			final ServerJoinMessage msg = (ServerJoinMessage) message;
			if (!msg.rejected) {
				Logger.getLogger(ClientNetListener.class.getName()).log(Level.INFO, "Login Erfolgreich");
				app.setStatusText("Connected!");
				app.enqueue(new Callable<Void>() {
					public Void call() throws Exception {
						worldManager.setMyPlayerId(msg.id);
						worldManager.setMyClientId(msg.client_id);
						app.startGame();
						return null;
					}
				});
			} else {
				Logger.getLogger(ClientNetListener.class.getName()).log(Level.INFO, "Ablehnung vom Server! Login fehlgeschlagen");
				app.setStatusText("Server hat den Login abgelehnt!");
			}
		} else if (message.getClass() == StartGameMessage.class) {
			final StartGameMessage msg = (StartGameMessage) message;
			if(!worldIsLoaded){ 
				worldIsLoaded = true;
				app.loadWorld(msg.scene, msg.models);
			}
		} else if (message.getClass() == ServerAddPlayerMessage.class) {
			app.updatePlayerData();
		} else if (message.getClass() == ServerRemovePlayerMessage.class) {
			app.updatePlayerData();
		} else if (message.getClass() == EntityActionMessage.class) {
			final EntityActionMessage msg = (EntityActionMessage) message;
			if(msg.getAction()==EntityActionMessage.PLAYER_INTERACT); //app.startChat(msg.getEntity_id());
		} else if (message.getClass() == ClientUserDataMessage.class) {
                        final ClientUserDataMessage msg = (ClientUserDataMessage) message;
                        app.updatePlayerList(msg);
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
        
        public void setO2(int o2) {
		this.O2 = o2;
	}

	public int getO2() {
		return O2;
	}

        public int getPulse() {
            return pulse;
        }

        public void setPulse(int pulse) {
            this.pulse = pulse;
            if(client != null && client.isConnected()){
                app.setStatusText("Sende Oxigen und Pulse to Server");
                client.send(new ClientUserDataMessage(client.getId(), this.name, this.O2, this.pulse));
                Logger.getLogger(ClientNetListener.class.getName()).log(Level.INFO, "gesendet Oxigen und Pulse to Server");
            }else{
                System.out.println("client down");
            }
        } 
}
