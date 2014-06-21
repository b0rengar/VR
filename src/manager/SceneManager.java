package manager;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Server;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.util.SkyFactory;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;


import controls.CharacterAnimControl;
import controls.UserCharacterControl;
import controls.MovementControl;
import controls.UserControl;
import messages.EntityActionMessage;
import messages.ImageMessage;
import messages.ServerAddEntityMessage;
import messages.ServerAddPlayerMessage;
import messages.ServerDisableEntityMessage;
import messages.ServerEnableEntityMessage;
import messages.ServerEnterEntityMessage;
import messages.ServerEntityDataMessage;
import messages.ServerRemoveEntityMessage;
import messages.ServerRemovePlayerMessage;
import persistence.Player;
import messages.SyncMessageValidator;
import messages.SyncMessage;
import util.ImageUtil;

/**
 * speichert und l�dt die Entit�ten
 * 
 */
@SuppressWarnings("deprecation")
public class SceneManager extends AbstractAppState implements SyncMessageValidator {

	private Server server;
	private Client client;
	private long myPlayerId = Long.MIN_VALUE;
	private long myClientId = Long.MIN_VALUE;
	private Node rootNode;
	private Node worldRoot;
	private HashMap<Long, Spatial> entities = new HashMap<Long, Spatial>();
	private long newId = 0;
	private AssetManager assetManager;
	private PhysicsSpace space;
	private List<Control> userControls = new LinkedList<Control>();
	private SyncManager syncManager;


	//nur zm testen erstmal hier
	static final int IMAGE_WIDTH = 480;
	static final int IMAGE_HEIGHT = 480;
	Image[] images;


	public SceneManager(Application app, Node rootNode) {
		this.rootNode = rootNode;
		this.assetManager = app.getAssetManager();
		this.space = app.getStateManager().getState(BulletAppState.class).getPhysicsSpace();
		this.client = app.getStateManager().getState(SyncManager.class).getClient();
		this.server = app.getStateManager().getState(SyncManager.class).getServer();
		syncManager = app.getStateManager().getState(SyncManager.class);
	}

	public boolean isServer() {
		return server != null;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public void addUserControl(Control control) {
		userControls.add(control);
	}

	public long getMyPlayerId() {
		return myPlayerId;
	}

	public void setMyPlayerId(long myPlayerId) {
		this.myPlayerId = myPlayerId;
	}

	public long getMyClientId() {
		return myClientId;
	}

	public void setMyClientId(long myClientId) {
		this.myClientId = myClientId;
	}

	public Node getWorldRoot() {
		return worldRoot;
	}

	public SyncManager getSyncManager() {
		return syncManager;
	}

	public PhysicsSpace getPhysicsSpace() {
		return space;
	}

	public void setUpLight() {
		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.White.mult(0.4f));
		rootNode.addLight(al);

		DirectionalLight sun = new DirectionalLight();
		rootNode.addLight(sun);
	}

	public void setUpSky() {  
		rootNode.attachChild(SkyFactory.createSky( assetManager,
				"Textures/bright/FullskiesBlueClear03.dds", false));
	}

	public void loadLevel(String name) {
		worldRoot = (Node) assetManager.loadModel(name);
		CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) worldRoot);
		RigidBodyControl rigidcontrol = new RigidBodyControl(sceneShape, 0.0f);
		worldRoot.addControl(rigidcontrol);
	}

	public void preloadModels(String[] modelNames) {
		for (int i = 0; i < modelNames.length; i++) {
			String string = modelNames[i];
			assetManager.loadModel(string);
		}
	}

	public void attachLevel() {
		space.addAll(worldRoot);
		rootNode.attachChild(worldRoot);
	}

	public long addNewPlayer(int groupId, String name, int aiId, int O2) {
		long playerId = Player.getNew(name);
		addPlayer(playerId, groupId, name, O2);
		return playerId;
	}

	public void addPlayer(long id, int clientId, String name, int O2) {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding player: "+ id);
		if (isServer())	syncManager.broadcast(new ServerAddPlayerMessage(id, name, clientId, O2));
		Player player = new Player(id, clientId, name, O2);
		player.setCharacter_entity_id(-1l);
		Player.add(id, player);
	}


	public void removePlayer(long id) {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Removing player: "+ id);
		if (isServer()) {
			syncManager.broadcast(new ServerRemovePlayerMessage(id));
			long entityId = Player.getPlayer(id).getEntity_id();
			if (entityId != -1) {
				enterEntity(id, -1);
			}
			long characterId = Player.getPlayer(id).getCharacter_entity_id();
			removeEntity(characterId);
		}
		Player.remove(id);
	}

	public Spatial getEntity(long id) {
		return entities.get(id);
	}

	public Spatial getMyEntity() {
		if (!isServer()) {
			for(Map.Entry<Long, Spatial> e : entities.entrySet()){
				long player_id = (Long) e.getValue().getUserData("player_id");
				if(player_id==myPlayerId)
					return e.getValue();
			}
		}
		return null;
	}

	public long getEntityId(Spatial entity) {
		for (Iterator<Entry<Long, Spatial>> it = entities.entrySet().iterator(); it.hasNext();) {
			Entry<Long, Spatial> entry = it.next();
			if (entry.getValue() == entity) {
				return entry.getKey();
			}
		}
		return -1;
	}

	public long getEntityId(PhysicsCollisionObject object) {
		Object obj = object.getUserObject();
		if (obj instanceof Spatial) {
			Spatial spatial = (Spatial) obj;
			if (spatial != null) {
				return getEntityId(spatial);
			}
		}
		return -1;
	}

	public long addNewEntity(String character, Vector3f location, Quaternion rotation, Vector3f scale, boolean broadcast) {
		newId++;
		addEntity(newId, character, location, rotation, scale, broadcast);
		return newId;
	}

	public void addEntity(long id, String modelIdentifier, Vector3f location, Quaternion rotation, Vector3f scale, boolean broadcast) {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding entity: "+ id);
		if (isServer() && broadcast) {
			Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Broadcast adding entity: "+ id);
			syncManager.broadcast(new ServerAddEntityMessage(id, modelIdentifier, location, rotation, scale));
		}
		//TODO alternative ueberlegen
		if(modelIdentifier.equals("Box")){
			addBox(id);
			return;
		}

		Node entityModel = (Node) assetManager.loadModel(modelIdentifier);
		setEntityTranslation(entityModel, location, rotation);
		entityModel.setLocalScale(scale);
		if (entityModel.getControl(CharacterControl.class) != null) {
			entityModel.addControl(new CharacterAnimControl());
			entityModel.getControl(CharacterControl.class).setFallSpeed(55.0f);
			entityModel.getControl(CharacterControl.class).setJumpSpeed(10.0f);
			entityModel.getControl(CharacterControl.class).setGravity(9.8f * 3);
			entityModel.getControl(CharacterControl.class).setMaxSlope(1.0f);
			entityModel.getControl(CharacterControl.class).setPhysicsLocation(new Vector3f(20,15,20));
		}
		entityModel.setUserData("model", modelIdentifier);
		entityModel.setUserData("player_id", -1l);
		entityModel.setUserData("group_id", -1);
		entityModel.setUserData("entity_id", id);
		entities.put(id, entityModel);
		syncManager.addObject(id, entityModel);
		space.addAll(entityModel);
		worldRoot.attachChild(entityModel);
	}

	//TODO nur zum testen erstmal so -> saubere Loesung finden (xml vlt)
	public void addBox(long id){
		if(id==-1){
			newId++;
			id = newId;
		}
		Box b = new Box(Vector3f.ZERO, 1, 1, 0.03f);
		Geometry geom = new Geometry("Box", b);
		Material geom_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		geom_mat.setColor("Color", ColorRGBA.LightGray);
		geom.setMaterial(geom_mat);
		Node n = new Node("Box");
		n.attachChild(geom);
		//Reihenfolge wichtig!
		n.addControl(new RigidBodyControl(0.0f));
		setEntityTranslation(n, new Vector3f(-25.66f , 5f, -48.25f), new Quaternion().fromAngles(0, -16, 0));
		n.setUserData("player_id", -1l);
		n.setUserData("group_id", -1);
		n.setUserData("entity_id", id);
		entities.put(id, n);
		syncManager.addObject(id, n);
		space.addAll(n);
		worldRoot.attachChild(n);
	}

	public void addOtherPlayer(int client_id, long character_entity_id) {
		Spatial entity = entities.get(character_entity_id);
		String model = (String) entity.getUserData("model");
		if(model != null) syncManager.send(client_id, new ServerAddEntityMessage(character_entity_id, model, entity.getLocalTranslation(), entity.getLocalRotation(), entity.getLocalScale()));
	}

	public void addModels(int client_id) {
		for(Map.Entry<Long, Spatial> e : entities.entrySet()){
			long entity_id = e.getKey();
			Spatial entity = e.getValue();
			if(entity.getControl(CharacterControl.class) == null){
				String model = (String) entity.getUserData("model");
				if(model!=null){
					syncManager.send(client_id, new ServerAddEntityMessage(entity_id, model, entity.getLocalTranslation(), entity.getLocalRotation(), entity.getLocalScale()));
				} else {
					//TODO alternative ueberlegen
					syncManager.send(client_id, new ServerAddEntityMessage(entity_id, "Box", entity.getLocalTranslation(), entity.getLocalRotation(), entity.getLocalScale()));
				}
			}
		}
	}

	public void removeEntity(long id) {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Removing entity: {0}", id);
		if (isServer()) {
			Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Broadcast removing entity: {0}", id);
			syncManager.broadcast(new ServerRemoveEntityMessage(id));
		}
		syncManager.removeObject(id);
		Spatial spat = entities.remove(id);
		if (spat == null) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "try removing entity thats not there: {0}", id);
			return;
		}
		Long playerId = (Long) spat.getUserData("player_id");
		removeTransientControls(spat);
		if (playerId == myPlayerId) {
			removeUserControls(spat);
		}
		//		if (playerId != -1) {
		//			Player.getPlayer(playerId).setEntity_id(-1l);
		//		}
		spat.removeFromParent();
		space.removeAll(spat);
	}

	public void disableEntity(long id) {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Disabling entity: {0}", id);
		if (isServer()) {
			Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Broadcast removing entity: {0}", id);
			syncManager.broadcast(new ServerDisableEntityMessage(id));
		}
		Spatial spat = getEntity(id);
		spat.removeFromParent();
		space.removeAll(spat);
	}

	public void enableEntity(long id, Vector3f location, Quaternion rotation) {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Enabling entity: "+ id);
		if (isServer()) {
			Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Broadcast removing entity: ", id);
			syncManager.broadcast(new ServerEnableEntityMessage(id, location, rotation));
		}
		Spatial spat = getEntity(id);
		setEntityTranslation(spat, location, rotation);
		worldRoot.attachChild(spat);
		space.addAll(spat);
	}

	private void setEntityTranslation(Spatial entityModel, Vector3f location, Quaternion rotation) {
		if (entityModel.getControl(RigidBodyControl.class) != null) {
			entityModel.getControl(RigidBodyControl.class).setPhysicsLocation(location);
			entityModel.getControl(RigidBodyControl.class).setPhysicsRotation(rotation.toRotationMatrix());
		} else if (entityModel.getControl(CharacterControl.class) != null) {
			CharacterControl control = entityModel.getControl(CharacterControl.class);
			control.setPhysicsLocation(location);
			//TODO anpassen
			control.getCollisionShape().setScale(new Vector3f(0.9f, 0.64f, 0.5f));
			//setCollisionShape(new CapsuleCollisionShape(1, 1, 1));
			control.setViewDirection(rotation.mult(Vector3f.UNIT_Z).multLocal(1, 0, 1).normalizeLocal());
		} else if (entityModel.getControl(VehicleControl.class) != null) {
			entityModel.getControl(VehicleControl.class).setPhysicsLocation(location);
			entityModel.getControl(VehicleControl.class).setPhysicsRotation(rotation.toRotationMatrix());
		} else {
			entityModel.setLocalTranslation(location);
			entityModel.setLocalRotation(rotation);
		}
	}

	public void enterEntity(long playerId, long entityId) {
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Player {0} entering entity {1}", new Object[]{playerId, entityId});
		if (isServer()) {
			syncManager.broadcast(new ServerEnterEntityMessage(playerId, entityId));
		}
		if(Player.getPlayer(playerId)==null)return;
		long curEntity = Player.getPlayer(playerId).getEntity_id();
		int groupId = Player.getPlayer(playerId).getGroup_id();
		if (curEntity != -1) {
			Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Player {0} exiting current entity {1}", new Object[]{playerId, curEntity});
			Spatial curEntitySpat = getEntity(curEntity);
			curEntitySpat.setUserData("player_id", -1l);
			curEntitySpat.setUserData("group_id", -1);
			removeTransientControls(curEntitySpat);
			if (playerId == myPlayerId) {
				removeUserControls(curEntitySpat);
			}
		}
		Player.getPlayer(playerId).setEntity_id(entityId);
		//if we entered an entity, configure its controls, id -1 means enter no entity
		if (entityId != -1) {
                    Node spat = (Node) getEntity(entityId);
                    spat.setUserData("player_id", playerId);
                    spat.setUserData("group_id", groupId);
                    BitmapFont fnt = assetManager.loadFont("Interface/Fonts/Default.fnt");
                    BitmapText txt = new BitmapText(fnt, false);
                    txt.setBox(new com.jme3.font.Rectangle(0, 0, 6, 3));
                    txt.setQueueBucket(Bucket.Transparent);
                    txt.setSize( 0.5f );
                    txt.setText(Player.getPlayer(playerId).getName());
    //	        txt.setAlpha(0.6f);
                    txt.setAlignment(Align.Center);
                    txt.setLocalTranslation(-3f , 6, 0.4f);
                    spat.attachChild(txt);
                    if (groupId == getMyClientId()) { //only true on clients
                            makeUserControl(entityId, client);
                            //move controls for local user to new spatial
                            if (playerId == getMyPlayerId()) {
//                                System.out.println(spat.getUserData("player_id"));
                                       
                                    addUserControls(spat);
                            }
                    } else {
                            makeUserControl(entityId, null);
                    }
		} 
	}

	private void makeUserControl(long entityId, Client client) {
		Spatial spat = getEntity(entityId);
		if (spat.getControl(CharacterControl.class) != null) {
			if (client != null) {
				if ((Integer) spat.getUserData("group_id") == myClientId) {
					spat.addControl(new UserCharacterControl(client, entityId));
				} else {
					spat.addControl(new UserCharacterControl());
				}
			} else {
				spat.addControl(new UserCharacterControl());
			}
		}
	}

	private void removeTransientControls(Spatial spat) {
		UserControl manualControl = spat.getControl(UserControl.class);
		if (manualControl != null) {
			spat.removeControl(manualControl);
		}
	}

	private void addUserControls(Spatial spat) {
		for (Iterator<Control> it = userControls.iterator(); it.hasNext();) {
			Control control = it.next();
//                        System.out.println("controll:");
//                        System.out.println(control.toString()); 
//			if (control!=null)
                            spat.addControl(control);
                        
		}
	}

	private void removeUserControls(Spatial spat) {
		for (Iterator<Control> it = userControls.iterator(); it.hasNext();) {
			Control control = it.next();
			spat.removeControl(control);
		}
	}

	public void setEntityUserData(long id, String name, Object data) {
		if (isServer()) {
			syncManager.broadcast(new ServerEntityDataMessage(id, name, data));
		}
		getEntity(id).setUserData(name, data);
	}


	public Spatial getTargetEntity(Spatial entity, float length, Vector3f storeLocation) {
		MovementControl control = entity.getControl(MovementControl.class);
		Vector3f startLocation = control.getLocation();
		Vector3f endLocation = startLocation.add(control.getAimDirection().normalize().multLocal(length));
		List<PhysicsRayTestResult> results = getPhysicsSpace().rayTest(startLocation, endLocation);
		Spatial found = null;
		float dist = Float.MAX_VALUE;
		for (Iterator<PhysicsRayTestResult> it = results.iterator(); it.hasNext();) {
			PhysicsRayTestResult physicsRayTestResult = it.next();
			Spatial spatial = null;
			Object obj = physicsRayTestResult.getCollisionObject().getUserObject();
			if (obj instanceof Spatial) {
				if (entities.containsValue((Spatial) obj))
					spatial = (Spatial) obj;
			}
			if (spatial == entity) continue;
			if (physicsRayTestResult.getHitFraction() < dist) {
				dist = physicsRayTestResult.getHitFraction();
				if (storeLocation != null) FastMath.interpolateLinear(physicsRayTestResult.getHitFraction(), startLocation, endLocation, storeLocation);
				found = spatial;
			}
		}
		return found;
	}


	public boolean checkMessage(SyncMessage message) {
		if (message.getSyncId() >= 0 && getEntity(message.getSyncId()) == null) return false;
		return true;
	}

	@Override
	public void update(float tpf) {	}

	public void doAction(long entity_id, int action){
		switch (action) {
		case EntityActionMessage.TRANSFORM:
			entities.get(entity_id).setLocalScale(entities.get(entity_id).getLocalScale().subtract(new Vector3f(0.03f,0.03f,0.03f)));
			break;
		case EntityActionMessage.PLAYER_INTERACT:
			break;
		}
	}

	public void sendAction(int client_id, long entity_id){
		if (isServer()) {
			syncManager.send(client_id, new EntityActionMessage(entity_id, EntityActionMessage.TRANSFORM));
		}
	}

	public void playerInteract(int client_id, long entity_id){
		if (isServer()) {
			syncManager.send(client_id, new EntityActionMessage(entity_id, EntityActionMessage.PLAYER_INTERACT));
		}
	}

	public void setNextImage(int client_id, int image_id, long entity_id) {
		if (isServer()) {
			if (image_id >= 0 && image_id < this.images.length) {
				entities.get(entity_id).setUserData("image_id", image_id);
				byte[] ib = getImageBytes(image_id);
				//				System.out.println(ib.length);
				if(ib.length>34000){
					return;
				}
				ImageMessage resp = new ImageMessage(entity_id, image_id, ib);
				Logger.getLogger(SceneManager.class.getName()).log(Level.INFO, "Neues Bild erstellt Image_ID: " + image_id);
				syncManager.send(client_id, resp);
			} 
		}
	}

	public void setNextImage(long entity_id, byte[] image_data) {
		Material geom_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Spatial sp = entities.get(entity_id);
		sp.setMaterial(geom_mat);
		Texture texture = new Texture2D();
		BufferedImage b;
		try {
			b = ImageUtil.getBufferedImage(ImageUtil.getImageFromBytes(ImageUtil.deCompress(image_data)));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		texture.setImage(new AWTLoader().load(b, true));
		geom_mat.setTexture("ColorMap", texture);
	}


	public void setTextureScale(Spatial spatial, Vector2f vector) {
		if (spatial instanceof Node) {
			Node findingnode = (Node) spatial;
			for (int i = 0; i < findingnode.getQuantity(); i++) {
				Spatial child = findingnode.getChild(i);
				setTextureScale(child, vector);
			}
		} else if (spatial instanceof Geometry) {
			((Geometry) spatial).getMesh().scaleTextureCoordinates(vector);
		}
	}

	public byte[] getImageBytes(int id){
		BufferedImage bi = ImageUtil.drawingToGrayscaleBufferedImage(images[id]);
		return ImageUtil.compress(ImageUtil.convertToPNG(bi));
	}
}
