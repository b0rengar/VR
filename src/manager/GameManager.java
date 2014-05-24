package manager;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import java.util.logging.Level;
import java.util.logging.Logger;
import messages.ActionMessage;
import persistence.Player;

@SuppressWarnings("deprecation")
public class GameManager extends AbstractAppState {

	private SyncManager server;
	private SceneManager sceneManager;
	private boolean isRunning = false;
	private String mapName = "Scenes/newScene.j3o";
	private String[] modelNames = new String[]{"Models/Sinbad/Sinbad.j3o"};

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		this.sceneManager = app.getStateManager().getState(SceneManager.class);
		this.setPhysicsSyncManager(sceneManager.getSyncManager());
	}

	public synchronized boolean startGame() {
		if (!isRunning) {
			isRunning = true;
			sceneManager.loadLevel(mapName);
			sceneManager.preloadModels(modelNames);
			sceneManager.attachLevel();
//			sceneManager.addNewEntity("Models/Ferrari/Car.j3o", new Vector3f(25, 5, 25), new Quaternion(), false);
//			sceneManager.addBox();
		}
		return true;
	}

	public synchronized boolean addEntity(int client_id) {
		for (Player player : Player.getPlayers()) {
			if(player.getGroup_id()==client_id){
				long character_entity_id = sceneManager.addNewEntity("Models/Sinbad/Sinbad.j3o", new Vector3f(20,15,20), new Quaternion(),new Vector3f(0.27f, 0.27f, 0.27f),true);
				player.setCharacter_entity_id(character_entity_id);
				sceneManager.enterEntity(player.getId(), character_entity_id);
			} else {
				sceneManager.addOtherPlayer(client_id, player.getCharacter_entity_id());
				sceneManager.enterEntity(player.getId(), player.getCharacter_entity_id());
			}
		}
		sceneManager.addModels(client_id);
		return true;
	}

	public void performAction(int client_id, long entityId, int action, boolean pressed) {
		Spatial myEntity = sceneManager.getEntity(entityId);
		if (myEntity == null) {
			Logger.getLogger(GameManager.class.getName()).log(Level.WARNING, "Action wurde nicht gefunden!");
			return;
		}
		long player_id = (Long) myEntity.getUserData("player_id");
		if (player_id == -1) {
			Logger.getLogger(GameManager.class.getName()).log(Level.WARNING, "Spieler-ID fuer die Action wurde nicht gefunden!");
			return;
		}
		if (action == ActionMessage.LEFT_CLICK_ACTION && pressed) {
			executeLeftClickAction(client_id, myEntity);
		} else if (action == ActionMessage.RIGHT_CLICK_ACTION && pressed) {
			executeRightClickAction(client_id, myEntity);
		}
	}

	private void executeLeftClickAction(int client_id, Spatial myEntity) {
		Vector3f clickLocation = new Vector3f();
		Spatial targetEntity = sceneManager.getTargetEntity(myEntity, 10, clickLocation);
		if (targetEntity != null) {
			long targetId = (Long) targetEntity.getUserData("entity_id");
			if (targetEntity.getName().equals("Box")) {
				Integer image_id = (Integer)targetEntity.getUserData("image_id");
				image_id = (image_id==null) ? 0 : image_id+1;
				sceneManager.setNextImage(client_id, image_id, targetId);
			} else if(targetEntity.getName().equals("Ferrari"))
				sceneManager.sendAction(client_id, targetId);
			
			if(targetEntity.getControl(CharacterControl.class) != null){		
				sceneManager.playerInteract(client_id, targetId);
			}
		}
	}
	
	private void executeRightClickAction(int client_id, Spatial myEntity) {
		Vector3f clickLocation = new Vector3f();
		Spatial targetEntity = sceneManager.getTargetEntity(myEntity, 10, clickLocation);
		if (targetEntity != null) {
			long targetId = (Long) targetEntity.getUserData("entity_id");
			if (targetEntity.getName().equals("Box")) {
				Integer image_id = (Integer)targetEntity.getUserData("image_id");
				image_id = (image_id==null) ? 0 : image_id-1;
				sceneManager.setNextImage(client_id, image_id, targetId);
			}
		}
	}

	public SyncManager getPhysicsSyncManager() {
		return server;
	}

	public void setPhysicsSyncManager(SyncManager server) {
		this.server = server;
	}

	public String getMapName() {
		return mapName;
	}

	public String[] getModelNames() {
		return modelNames;
	}


}
