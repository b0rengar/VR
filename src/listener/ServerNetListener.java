package listener;

import com.jme3.network.Server;
import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.MessageListener;
import com.jme3.network.Message;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.ServerMain;
import config.Settings;
import manager.GameManager;
import manager.SceneManager;
import messages.ActionMessage;
import messages.ClientUserDataMessage;
import messages.ClientJoinMessage;
import messages.HandshakeMessage;
import messages.ServerAddPlayerMessage;
import messages.ServerJoinMessage;
import messages.StartGameMessage;
import persistence.Player;
import persistence.ServerClientData;
import com.jme3.math.Vector3f;
import logging.UserDataLog;

/**
 * listener f�r den Netzwerk-Nachrichten-Transfer
 * 
 */
public class ServerNetListener implements MessageListener<HostedConnection>, ConnectionListener {

	ServerMain app;
	com.jme3.network.Server server;
	SceneManager worldManager;
	GameManager gameManager;
        
        UserDataLog usl;
	

	public ServerNetListener(ServerMain app, Server server, SceneManager worldManager, GameManager gameManager) {
		this.server = server;
		this.worldManager = worldManager;
		this.app = app;
		this.gameManager = gameManager;
		server.addConnectionListener(this);
		server.addMessageListener(this, HandshakeMessage.class, ClientJoinMessage.class, StartGameMessage.class, ActionMessage.class, ClientUserDataMessage.class);
                usl = new UserDataLog();
        }

	public void connectionAdded(Server serverr, HostedConnection client) {
		int clientId = (int) client.getId();
		if (!ServerClientData.exsists(clientId)) {
			ServerClientData.add(clientId);
		} else {
			Logger.getLogger(ServerNetListener.class.getName()).log(Level.SEVERE, "Client-ID existiert bereits!");
			return;
		}
	}

	public void connectionRemoved(Server serverr, HostedConnection client) {
		final int clientId = (int) client.getId();
		final long playerId = ServerClientData.getPlayerId(clientId);
		ServerClientData.remove(clientId);
		app.enqueue(new Callable<Void>() {
			public Void call() throws Exception {
				//				String name = Player.getPlayer(playerId).getName();
				worldManager.removePlayer(playerId);
				//				server.broadcast(new ChatMessage("Server", name + " left the game"));
				//				Logger.getLogger(ServerNetListener.class.getName()).log(Level.INFO, "Broadcast player left message");
				//				if (Player.getPlayers().isEmpty()) {
				//                    gameManager.stopGame();
				//				}
				return null;
			}
		});
	}

	public void messageReceived(HostedConnection source, Message message) {
       		if (message.getClass() == HandshakeMessage.class) {
			HandshakeMessage msg = (HandshakeMessage) message;
			Logger.getLogger(ServerNetListener.class.getName()).log(Level.INFO, "Handshake-Nachricht bekommen");
			if (msg.getProtocol_version() != Settings.getInstance().getProtocol_version()) {
				source.close("Connection Protocol Mismatch - Update Client");
				Logger.getLogger(ServerNetListener.class.getName()).log(Level.INFO, "Falsche Client-Protokoll-Version, Verbindung wird getrennt");
				return;
			}
			source.send(msg);
			Logger.getLogger(ServerNetListener.class.getName()).log(Level.INFO, "Sende Handshake-Nachricht zurueck");
		} else if (message.getClass() == ClientJoinMessage.class) {
			final ClientJoinMessage msg = (ClientJoinMessage) message;
			Logger.getLogger(ServerNetListener.class.getName()).log(Level.INFO, "Ein Client moechte dem Spiel beitreten");
			final int clientId = (int) source.getId();
			if (!ServerClientData.exsists(clientId)) {
				Logger.getLogger(ServerNetListener.class.getName()).log(Level.WARNING, "Join-Nachricht von einem unbekannten Client erhalten");
				return;
			}
			final long newPlayerId = Player.getNew(msg.getName());
			Logger.getLogger(ServerNetListener.class.getName()).log(Level.INFO, "Neue Spieler-ID erstellt: "+ newPlayerId);
			ServerClientData.setConnected(clientId, true);
			ServerClientData.setPlayerId(clientId, newPlayerId);
			ServerJoinMessage serverJoinMessage = new ServerJoinMessage(newPlayerId, clientId, msg.getName(), false, msg.getO2());
			//TODO bei allen Spielern anzeigen -> neuer Spieler
			//            server.broadcast(new ChatMessage("Server", msg.getName() + " joined the game"));
			source.send(serverJoinMessage);
			Logger.getLogger(ServerNetListener.class.getName()).log(Level.INFO, "Login Erfolgreich");
			//Spieler hinzufuegen
			app.enqueue(new Callable<Void>() {
				public Void call() throws Exception {
					worldManager.addPlayer(newPlayerId, clientId, msg.getName(), msg.getO2());
					for(Player player : Player.getPlayers()){
						if (player.getId() != newPlayerId) {
							worldManager.getSyncManager().send(clientId, new ServerAddPlayerMessage(player.getId(), player.getName(), player.getGroup_id(), player.getO2()));
							Logger.getLogger(ServerNetListener.class.getName()).log(Level.INFO, "Sende Spieler "+new Object[]{player.getId()+" zu Client "+newPlayerId});
						}
					}
					return null;
				}
			});
		} else if (message.getClass() ==  StartGameMessage.class) {
			source.send(new StartGameMessage(gameManager.getMapName(), gameManager.getModelNames()));
			final int clientId = (int) source.getId();
			app.enqueue(new Callable<Void>() {
				public Void call() throws Exception {
					gameManager.startGame();
					gameManager.addEntity(clientId);
					return null;
				}
			});
		} else if (message.getClass() ==  ActionMessage.class) {
			final ActionMessage msg = (ActionMessage) message;
			final int clientId = (int) source.getId();
			app.enqueue(new Callable<Void>() {
				public Void call() throws Exception {
					gameManager.performAction(clientId, msg.getSyncId(), msg.getAction(), msg.isPressed());
					return null;
				}
			});
		} else if (message.getClass() ==  ClientUserDataMessage.class) {
                        //System.out.println("message detected");
			final ClientUserDataMessage msg = (ClientUserDataMessage) message;
			//final int clientId = (int) source.getId();
                        app.enqueue(new Callable<Void>() {
				public Void call() throws Exception {
					for(Player player : Player.getPlayers()){
                                            if((player.getName()).equals(msg.getPlayerName())){
                                                player.setO2(msg.getO2());
                                                player.setPulse(msg.getPulse()); 
                                                player.setLocation(msg.getLocation());
                                                //System.out.println(player.getName() + player.getO2() + player.getPulse());
                                            }
                                        }
                                        for(Player player : Player.getPlayers()){
                                            //System.out.println(player.getName() + player.getO2() + player.getPulse());
                                            server.broadcast(new ClientUserDataMessage(player.getName(),player.getO2(),player.getPulse(),player.getLocation()));
                                            Logger.getLogger(ServerNetListener.class.getName()).log(Level.INFO, "Sende Broadcast with UserData");
					}
                                        usl.writeLogFile(msg);
					return null;
				}
			});
		}
//		else if (message instanceof ImageMessage) {
//			ImageMessage msg = (ImageMessage) message;
//			int id = msg.getImage_id();
//			ImageMessage resp;
//			if (id >= 0 && id < this.images.length) {
//				resp = new ImageMessage(-1, id, getImageBytes(id));
//				Logger.getLogger(ServerNetListener.class.getName()).log(Level.INFO, "Neues Bild erstellt Image_ID: " + id);
//				source.send(resp);
//			} 
//		}
	}


	/**
	 * checks if the message client is valid, meaning logged in
	 * @param message
	 * @return
	 */
	private boolean checkClient(int clientId, Message message) {
		if (ServerClientData.exsists(clientId) && ServerClientData.isConnected(clientId)) {
			return true;
		} else {
			Logger.getLogger(ServerNetListener.class.getName()).log(Level.WARNING, "Ungueltige Clientdaten erhalten: Client-ID: "+ clientId);
			return false;
		}
	}
}
