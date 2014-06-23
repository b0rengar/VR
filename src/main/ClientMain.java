package main;

import com.jme3.animation.AnimControl;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.network.NetworkClient;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.jme3.ui.Picture;
import config.Settings;
import config.Setup;

import controls.UserInputControl;
import datamodel.building.H14;
import datamodel.lamps.Lamp;
import datamodel.lamps.LampChangeListener;
import datamodel.lamps.LampManager;
import datamodel.sensors.Sensor;
import datamodel.sensors.SensorChangeListener;
import datamodel.sensors.SensorManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.textfield.TextFieldControl;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import event.FireAlarmSystemEventTypes;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import listener.ClientNetListener;
import manager.SceneManager;
import manager.SyncManager;
import messages.ActionMessage;
import messages.ControlMessage;
import messages.EntityActionMessage;
import messages.ServerAddEntityMessage;
import messages.ServerAddPlayerMessage;
import messages.ServerDisableEntityMessage;
import messages.ServerEnableEntityMessage;
import messages.ServerEnterEntityMessage;
import messages.ServerEntityDataMessage;
import messages.ServerRemoveEntityMessage;
import messages.ServerRemovePlayerMessage;
import messages.StartGameMessage;
import messages.SyncCharacterMessage;
import messages.SyncRigidBodyMessage;
import messages.ClientUserDataMessage;
import persistence.Player;

/**
 * test
 * @author fibu
 */
public class ClientMain extends SimpleApplication implements ScreenController, SensorChangeListener, LampChangeListener{
    Client myClient = null;
    private static ClientMain app;  
        
        private int fiveSec = 2;
        private boolean PlayerTab = false;
        private boolean blnUserData = false;
	private SceneManager sceneManager;
	private SyncManager syncManager;
	private Nifty nifty;
	private NiftyJmeDisplay niftyDisplay;
	private TextRenderer statusText;
        private TextRenderer playerLine;
	private NetworkClient client;
	private ClientNetListener clientNetListener;
	private BulletAppState bulletState;
	private BitmapText crossHair;
	private UserInputControl userInputControl;
        
        private Geometry mark;
        private HashMap<Sensor,ParticleEmitter> sensorMap;
        private HashMap<Lamp, Spatial> lampMap;
        private Spatial sensorObject;
        private Spatial lampObject;
        private ParticleEmitter fire;
        private int floor = 0;
        Picture mapPic;
        BitmapText mapText;
        private HashMap<Long, BitmapText> mapTextOthers = new HashMap<Long, BitmapText>();
        private boolean playerInH14 = false;
//        private Vector3f lastLocation = null;
//        private Vector3f currentLocation = null;
        private long lastTime = 0;
        private long currentTime = 0;
        private H14 h14;
        
        private SensorManager sensorManager;
        private LampManager lampManager;

    
    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setResolution(Settings.getInstance().getScene_resolution_width(),Settings.getInstance().getScene_resolution_height());
        settings.setSettingsDialogImage("/Interface/Images/logo.png");
        settings.setTitle("");
        Setup.registerSerializers();
        Setup.setLogLevels(Settings.getInstance().isDebug());
        app = new ClientMain();
        app.setSettings(settings);
        app.setPauseOnLostFocus(false);
        
        //app.start(); //startet mit dialog
        
        app.start(JmeContext.Type.Display); // standard display type
    }
  
    @Override
    public void simpleInitApp() {
        initMark();
        h14 = new H14();
        setDisplayFps(false);
        setDisplayStatView(false);
        startNifty();
        client = Network.createClient();
        bulletState = new BulletAppState();
        bulletState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        getStateManager().attach(bulletState);
        bulletState.getPhysicsSpace().setAccuracy(Settings.getInstance().getPhysics_fps());
        inputManager.setCursorVisible(true);
        flyCam.setEnabled(false);
        syncManager = new SyncManager(app, client);
        syncManager.setMaxDelay(Settings.getInstance().getNetw_max_physicsdelay());
        syncManager.setMessageTypes(
            ControlMessage.class,
            ActionMessage.class,
            SyncCharacterMessage.class,
            SyncRigidBodyMessage.class,
            ServerEntityDataMessage.class,
            ServerEnterEntityMessage.class,
            ServerAddEntityMessage.class,
            ServerAddPlayerMessage.class,
            ServerEnableEntityMessage.class,
            ServerDisableEntityMessage.class,
            ServerRemoveEntityMessage.class,
            ServerRemovePlayerMessage.class,
            EntityActionMessage.class);
        stateManager.attach(syncManager);
        sceneManager = new SceneManager(this, rootNode);
//        sceneManager.addUserControl(new HUDControl(nifty.getScreen("hud")));
        userInputControl = new UserInputControl(inputManager, cam);
        sceneManager.addUserControl(userInputControl);
        stateManager.attach(sceneManager);
        syncManager.addObject(-1, sceneManager);
        
        SensorManager.getInstance().setClient(client);
        LampManager.getInstance().setClient(client);
        clientNetListener = new ClientNetListener(this, client, sceneManager);
   }
    
    private void startNifty() {
		guiNode.detachAllChildren();
		guiNode.attachChild(fpsText);
		niftyDisplay = new NiftyJmeDisplay(assetManager,
				inputManager,
				audioRenderer,
				guiViewPort);
		nifty = niftyDisplay.getNifty();
		try {
			nifty.fromXml("Interface/clientUI.xml", "load_game", this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		statusText = nifty.getScreen("load_game").findElementByName("layer").findElementByName("panel").findElementByName("status_text").getRenderer(TextRenderer.class);
		guiViewPort.addProcessor(niftyDisplay);

    }

    public void setStatusText(final String text) {
            enqueue(new Callable<Void>() {
                    public Void call() throws Exception {
                            statusText.setText(text);
                            return null;
                    }
            });
    }

    public void updatePlayerData() {
            Logger.getLogger(ClientMain.class.getName()).log(Level.INFO, "Aktualisiere Playerdaten");
            enqueue(new Callable<Void>() {
                    public Void call() throws Exception {
                            for (Player player : Player.getPlayers()) {
                                    Logger.getLogger(ClientMain.class.getName()).log(Level.INFO, "List player {0}", player);
                            }
                            return null;
                    }
            });
    }


    public void connect() {
            final String userName = nifty.getScreen("load_game").findElementByName("layer").findElementByName("panel").findElementByName("username_text").getControl(TextFieldControl.class).getText();
            final String userO2 = nifty.getScreen("load_game").findElementByName("layer").findElementByName("panel").findElementByName("usero2_text").getControl(TextFieldControl.class).getText();
            final String serverIPstr = nifty.getScreen("load_game").findElementByName("layer").findElementByName("panel").findElementByName("serverip_text").getControl(TextFieldControl.class).getText();
            int userO2int = 0;
            InetAddress serverIP = null;
            try {
                serverIP = InetAddress.getByName(serverIPstr);
                userO2int = Integer.parseInt(userO2);
            } catch (UnknownHostException ex) {
                Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
                setStatusText("IP address invalid");
                return;
            } catch (Exception ex) {
                userO2int = 300;
            }           
            if (userName.trim().length() == 0) {
                    setStatusText("Username invalid");
                    return;
            }
            clientNetListener.setName(userName);
            clientNetListener.setO2(userO2int); //try if server messages are working
            statusText.setText("Connecting..");
            try {
                    client.connectToServer(serverIP, Settings.getInstance().getPort_tcp(), Settings.getInstance().getPort_udp());
                    client.start();
            } catch (IOException ex) {
                    setStatusText(ex.getMessage());
                    Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, ex.getMessage());
            }
    }

    public void startGame() {
            inputManager.setCursorVisible(true);
            client.send(new StartGameMessage());
    }

    public void loadWorld(final String name, final String[] modelNames) {
            final TextRenderer statusText = nifty.getScreen("load_world").findElementByName("load_world_panel").findElementByName("status_text").getRenderer(TextRenderer.class);
            inputManager.setCursorVisible(false);
            try {
                    enqueue(new Callable<Void>() {
                            public Void call() throws Exception {
                                    nifty.gotoScreen("load_world");
                                    statusText.setText("Lade Campus");
                                    return null;
                            }
                    }).get();
                    sceneManager.loadLevel(name);
                    enqueue(new Callable<Void>() {
                            public Void call() throws Exception {
                                    statusText.setText("Lade Models");
                                    return null;
                            }
                    }).get();
                    sceneManager.preloadModels(modelNames);
                    enqueue(new Callable<Void>() {
                            public Void call() throws Exception {
                                    setUpSensors();
                                    setUpLamps();
                                    sceneManager.attachLevel();
                                    //sceneManager.addBox();
                                    statusText.setText("Fertig!");
                                    nifty.removeScreen("load_world");
                                    initCrossHair();
                                    initKeys();
                                    return null;
                            }
                    }).get();
            } catch (Exception e) {
                    e.printStackTrace();
            }

            new Thread(new Runnable() {
                    public void run() {
                            setScreenMode(true);
                            try{Thread.sleep(2000);}catch(Exception e){}
                            nifty.gotoScreen("start");
                            inputManager.setCursorVisible(true);
                    }
            }).start();
            setUpMap();            
    }
        
    public void bind(Nifty nifty, Screen screen) {}
    public void onStartScreen() {}
    public void onEndScreen() {}
    
    @Override
    public void simpleUpdate(float tpf) {
        if(lastTime == 0){
           lastTime = System.currentTimeMillis();
//            lastLocation = currentLocation;
//            System.out.println("TESTEST");
        } else {
            currentTime = System.currentTimeMillis();
            if(currentTime - lastTime > 3000){
//                lastLocation = currentLocation;
                lastTime = currentTime;
                Vector3f currentLocation = cam.getLocation();
                currentLocation.x = currentLocation.x * -1;
                currentLocation.z = currentLocation.z * -1;
                
                playerInH14 = h14.playerInBuildung(currentLocation);
                setUpMap();
                reCalculateUserData();
            }
        }       
    }

    @Override
    public void simpleRender(RenderManager rm) {}
    
    @Override
	public void destroy() {
		try {
			if(client != null)
				client.close();
			super.destroy();
		} catch (Exception ex) {
			Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
		}
		super.destroy();
	}

	protected void initCrossHair() {
		guiNode.detachAllChildren();
		guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
		BitmapText crossHair = new BitmapText(guiFont, false);
		crossHair.setSize(guiFont.getCharSet().getRenderedSize() * 0.8f);
		crossHair.setText("");
		float x = settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2;
		float y = settings.getHeight() / 2 + crossHair.getLineHeight() / 2;
		crossHair.setLocalTranslation(x, y, 0);
		guiNode.attachChild(crossHair);
		this.crossHair = crossHair;

	}

	public void setScreenMode(boolean enabled){
		inputManager.setCursorVisible(enabled);
		userInputControl.setEnabled(!enabled);
	}

	public void backtogame(){
		setScreenMode(false);
		nifty.gotoScreen("hud");
		crossHair.setText("(-+-)");
                blnUserData = true;
	}

	public NetworkClient getClient() {
		return client;
	}
        
        /** Defining the "Shoot" action: Determine what was hit and how to respond. */
        private ActionListener actionListener2 = new ActionListener() {

          public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Shoot") && !keyPressed) {
              // 1. Reset results list.
              CollisionResults results = new CollisionResults();
              // 2. Aim the ray from cam loc to cam direction.
              Ray ray = new Ray(cam.getLocation(), cam.getDirection());
              // 3. Collect intersections between Ray and Shootables in results list.
              sceneManager.getWorldRoot().collideWith(ray, results);
              // 4. Print the results
              System.out.println("----- Collisions? " + results.size() + "-----");
//              for (int i = 0; i < results.size(); i++) {
//                // For each hit, we know distance, impact point, name of geometry.
//                float dist = results.getCollision(i).getDistance();
//                Vector3f pt = results.getCollision(i).getContactPoint();
//                String hit = results.getCollision(i).getGeometry().getName();
//                System.out.println("* Collision #" + i);
//                System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
//              }
              // 5. Use the results (we mark the hit object)
              if (results.size() > 1) {
                // The closest collision point is what was truly hit:
                CollisionResult closest = results.getCollision(1);
                // For each hit, we know distance, impact point, name of geometry.
                float dist = closest.getDistance();
                Vector3f pt = closest.getContactPoint();
                double x = -1 * pt.getX();
                double z = -1 * pt.getZ();
                pt.setX((float)x);
                pt.setZ((float)z);
                String hit = closest.getGeometry().getName();
                System.out.println("* Collision #");
                System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
                // Let's interact - we mark the hit with a red dot.
//                mark.setLocalTranslation(closest.getContactPoint());
                mark.setLocalTranslation(pt);
//                rootNode.attachChild(mark);
                sceneManager.getWorldRoot().attachChild(mark);
                
              } else {
                // No hits? Then remove the red mark.
                sceneManager.getWorldRoot().detachChild(mark);
//                rootNode.detachChild(mark);
              }
            }
          }
        };
        
        /** A red ball that marks the last spot that was "hit" by the "shot". */
        protected void initMark() {
            Sphere sphere = new Sphere(30, 30, 0.2f);
            mark = new Geometry("BOOM!", sphere);
            Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mark_mat.setColor("Color", ColorRGBA.Red);
            mark.setMaterial(mark_mat);
        }
        
        private void initSensor(){
            sensorMap = new HashMap<Sensor, ParticleEmitter>();
            sensorObject = assetManager.loadModel("Models/Tools/rauchmelder2x.j3o");
        }
        
        private void initLamp(){
            lampMap = new HashMap<Lamp, Spatial>();
            lampObject = assetManager.loadModel("Models/lampe/lampe.j3o");
        }
        
        private void initFire(){
            fire = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
            Material mat_red = new Material(assetManager,"Common/MatDefs/Misc/Particle.j3md");
            mat_red.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
            fire.setMaterial(mat_red);
            fire.setImagesX(2); 
            fire.setImagesY(2); // 2x2 texture animation
            fire.setEndColor(  new ColorRGBA(1f, 0f, 0f, 1f));   // red
            fire.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
            fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
            fire.setStartSize(1.5f);
            fire.setEndSize(0.1f);
            fire.setGravity(0, 0, 0);
            fire.setLowLife(1f);
            fire.setHighLife(3f);
            fire.getParticleInfluencer().setVelocityVariation(0.3f);
        }
        
        /** Declaring the "Shoot" action and mapping to its triggers. */
        private void initKeys() {
          inputManager.addMapping("Shoot",new KeyTrigger(KeyInput.KEY_M)); // trigger 1: spacebar
//            new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 2: left-button click
          inputManager.addMapping("blow", new KeyTrigger((KeyInput.KEY_E)));
          inputManager.addListener(actionListener2, "Shoot");
          inputManager.addListener(actionListenerList, "blow");
          inputManager.addMapping("List", new KeyTrigger(KeyInput.KEY_TAB));
          inputManager.addListener(actionListenerList, "List");          
        }
        
        private void setUpMap(){
            if(playerInH14){
                if(mapPic == null){
                    mapPic = new Picture ("HUD Picture");
            //        if(floor == 0){
            //          mapPic.setImage(assetManager, "Textures/building_map_small_50.png", true);
            //        } else if (floor == 1){
            //          mapPic.setImage(assetManager, "Textures/building_map_50.png", true);
            //        } else{
            //          mapPic.setImage(assetManager, "Textures/ColoredTex/Monkey.png", true);
            //        }
                    mapPic.setImage(assetManager, "Textures/map/H14_EG_nur Raumnummerierung_dwg.png", true);

                    mapPic.setWidth(476);
                    mapPic.setHeight(173);
                    mapPic.setPosition(settings.getWidth() - 476 ,0);
                    guiNode.attachChild(mapPic);           
                }
                double yLoc = cam.getLocation().y;
                if(yLoc > -0.5 && yLoc < 3.0 && floor != 0){
                    floor = 0;
                    mapPic.setImage(assetManager, "Textures/map/H14_EG_nur Raumnummerierung_dwg.png", true);
                } else if(yLoc > 3.2 && yLoc < 6.0 && floor != 1){
                    floor = 1;
                    mapPic.setImage(assetManager, "Textures/map/H14_1.OG_nur Raumnummerierung.png", true);
                } else if(yLoc > 6.5 && yLoc < 10.0 && floor != 2){
                    floor = 2;
                    mapPic.setImage(assetManager, "Textures/map/H14_2_OG_nur Raumnummerierung_dwg.png", true);
                }
                System.out.println("FLOOR = " + floor);
                if(mapText != null)
                    guiNode.detachChild(mapText);
                mapText = new BitmapText(guiFont, false);          
                mapText.setSize(guiFont.getCharSet().getRenderedSize());      // font size
                mapText.setColor(ColorRGBA.Red);                             // font color
                mapText.setText("*");                             // the text
                Point2D.Double p = h14.getPlayerLocationOnMap(cam.getLocation());
                mapText.setLocalTranslation(settings.getWidth() - (476 - (int)p.x), (int)p.y, 0); // position
//              hudText.setLocalTranslation(settings.getWidth() - 400, 100, 0);
                guiNode.attachChild(mapText);
                for(Long elem : mapTextOthers.keySet()){
                    BitmapText mapTextOther = mapTextOthers.get(elem);
                    if(mapTextOther != null)
                        guiNode.detachChild(mapTextOther);
                }
                mapTextOthers.clear();
                for(Player player : Player.getPlayers()){
                    if(!(clientNetListener.getName()).equals(player.getName())){
                        if(player.getLocation() != null){
                            BitmapText mapTextOther = new BitmapText(guiFont,false);
                            mapTextOther.setSize(guiFont.getCharSet().getRenderedSize());      // font size
                            mapTextOther.setColor(ColorRGBA.Green);                             // font color
                            mapTextOther.setText("*");                             // the text
                            Point2D.Double pOther = h14.getPlayerLocationOnMap(player.getLocation());
                            mapTextOther.setLocalTranslation(settings.getWidth() - (476 - (int)pOther.x), (int)pOther.y, 0); // position
            //              hudText.setLocalTranslation(settings.getWidth() - 400, 100, 0);
                            mapTextOthers.put(player.getId(), mapTextOther);
                            guiNode.attachChild(mapTextOther);
                        }
                    }
                }
            } else {
                if(mapPic != null){
                    guiNode.detachChild(mapPic);
                    if(mapText != null)
                        guiNode.detachChild(mapText);
                    mapPic = null;
                }
            }
        }
        private ActionListener actionListenerList = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            int cnt = 1;
            Integer cntInt;
            if (name.equals("List") && !keyPressed) {
                if(PlayerTab == false){    
                    nifty.fromXmlWithoutStartScreen("Interface/clientUI.xml");                   
                    PlayerTab = true;
                    for (Player player : Player.getPlayers()) {
                        //System.out.println("Play: " + player.getName());
                        cntInt = new Integer (cnt);
                        playerLine = nifty.getScreen("playerTab").findElementByName("layer").findElementByName("panel").findElementByName(cntInt.toString()).getRenderer(TextRenderer.class);
                        playerLine.setText(player.getName() + "                                               " + player.getO2() + "                                          " + player.getPulse());
                        cnt++;
                    }
                    nifty.gotoScreen("playerTab");
                }else{
                    nifty.removeScreen("playerTab");
                    playerLine = nifty.getScreen("userDetails").findElementByName("layer").findElementByName("panel").findElementByName("name").getRenderer(TextRenderer.class);
                    playerLine.setText( clientNetListener.getName());
                    playerLine = nifty.getScreen("userDetails").findElementByName("layer").findElementByName("panel").findElementByName("oxigen").getRenderer(TextRenderer.class);
                    playerLine.setText("Oxigen: " + clientNetListener.getO2());
                    playerLine = nifty.getScreen("userDetails").findElementByName("layer").findElementByName("panel").findElementByName("pulse").getRenderer(TextRenderer.class);
                    playerLine.setText("Oxigen: " + clientNetListener.getO2());
                    playerLine = nifty.getScreen("userDetails").findElementByName("layer").findElementByName("panel").findElementByName("pulse").getRenderer(TextRenderer.class);
                    playerLine.setText("Pulse: " + clientNetListener.getPulse());
                    nifty.gotoScreen("userDetails");
                    PlayerTab = false;
                }
            }
            if (name.equals("blow") && !keyPressed) {
                List<Sensor> sensors = sensorManager.getSensors();
                Vector3f location = cam.getLocation();
                for(Sensor sensor : sensors){
//                    System.out.println("Loc = " + location.x + " x " + location.y +
//                            " --> sensorLoc : " + sensor.getX() + " x " + sensor.getZ());
                    double distance = Math.sqrt(Math.pow(-location.x - sensor.getX(), 2.0) + Math.pow(-location.z - sensor.getZ(), 2.0));
//                    System.out.println(distance);
                    if(distance < 3.0){
//                        sensorMap.get(s)
                        if(sensorMap.get(sensor) != null){
                            sceneManager.getWorldRoot().detachChild(sensorMap.get(sensor));
                            sensor.extinguish();
                            sensorMap.put(sensor, null);
                        }
                    }
                }
            }
        }
    };
        private void reCalculateUserData(){
            if(blnUserData == true){
                if(fiveSec == 2){
                    fiveSec = 0;
                    int O2 = clientNetListener.getO2();
                    if(O2 > 0){
                        O2 = O2 - 1;
                    }
                    clientNetListener.setO2(O2);
                    int oldPulse = clientNetListener.getPulse();
                    double pulse = oldPulse + (Math.random() * 20 - 10);
                    if(pulse < 80.0){
                        pulse = 80.0;
                    }
                    if(pulse > 190){
                        pulse = 190.0;
                    }
                    Vector3f loc = cam.getLocation();
                    clientNetListener.setLocation(loc);
                    clientNetListener.setPulse((int)pulse);
                    clientNetListener.sendUserData();
                    if(PlayerTab == false){
                        playerLine = nifty.getScreen("userDetails").findElementByName("layer").findElementByName("panel").findElementByName("name").getRenderer(TextRenderer.class);
                        playerLine.setText( clientNetListener.getName());
                        playerLine = nifty.getScreen("userDetails").findElementByName("layer").findElementByName("panel").findElementByName("oxigen").getRenderer(TextRenderer.class);
                        playerLine.setText("Oxigen: " + clientNetListener.getO2());
                        playerLine = nifty.getScreen("userDetails").findElementByName("layer").findElementByName("panel").findElementByName("pulse").getRenderer(TextRenderer.class);
                        playerLine.setText("Pulse: " + clientNetListener.getPulse());
                        nifty.gotoScreen("userDetails");
                    }
                }else{
                    fiveSec++;
                }
            }
        }
        
        public void updatePlayerList(ClientUserDataMessage msg){
           for(Player player : Player.getPlayers()){
                if((player.getName()).equals(msg.getPlayerName())){
                    player.setO2(msg.getO2());
                    player.setPulse(msg.getPulse());
                    player.setLocation(msg.getLocation());
                    //System.out.println("location: " + player.getLocation().toString());
                }
            }
        }

        private void setUpSensors(){
            sensorManager = SensorManager.getInstance();
            sensorManager.addSensorChangeListener(this);
            initSensor();
            initFire();
            List<Sensor> sensors = sensorManager.getSensors();
            System.out.println("Sensor List:");
            Vector3f pt;
            Spatial sensObj;
            for(Sensor sensor : sensors){
                System.out.println(sensor.getGroup() + " --> " + sensor.getId() + " --> " + sensor.getX() + " , " + sensor.getY() + " , " + sensor.getZ());
                pt = new Vector3f((float)sensor.getX(),(float)sensor.getY(),(float)sensor.getZ());
                sensObj = sensorObject.clone();
                sensObj.setLocalTranslation(pt);
                sensorMap.put(sensor, null);
                sceneManager.getWorldRoot().attachChild(sensObj);
                setFire(sensor);
            }            
        }

    public void sensorChanged(Sensor sensor) {
        setFire(sensor);
    }
    
    private void setFire(final Sensor sensor){
        
        enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                System.out.println("setFire");
                if(sensor.getStatus() == FireAlarmSystemEventTypes.ALARM){
                    ParticleEmitter fireEmitter;
                    if(sensorMap.get(sensor) == null){
                        System.out.println("Set FLAME");
                        fireEmitter = fire.clone();
                        fireEmitter.setLocalTranslation((float)sensor.getX(),(float)sensor.getY() - 3.0f,(float)sensor.getZ());
        //                fireEmitter.
                        fireEmitter.setStartSize((1.5f*(float)sensor.getFireSeverity())/100);
                        sensorMap.put(sensor, fireEmitter);
                        sceneManager.getWorldRoot().attachChild(fireEmitter);
                    } else {
                        System.out.println("Feuer wird kleienr --> " + (1.5f*(float)sensor.getFireSeverity())/100 );
                        fireEmitter = sensorMap.get(sensor);
                        fireEmitter.setStartSize((1.5f*(float)sensor.getFireSeverity())/100);
                    }
                } else {
                    if(sensorMap.get(sensor) != null){
                        System.out.println("Feuer gelöscht");
                        sceneManager.getWorldRoot().detachChild(sensorMap.get(sensor));
                        sensorMap.put(sensor, null);
                    }
                }
                return null;
            }
        });
    }
    
    private void setUpLamps(){
        lampManager = LampManager.getInstance();
        initLamp();
        
        List<Lamp> lamps = lampManager.getLamps();
        System.out.println("Lamp List:");
        Vector3f pt;
        Spatial lampObj;
        
        for(Lamp lamp : lamps){
            System.out.println("Lamp attributes ..");
            pt = lamp.getLocationVector();
            lampObj = lampObject.clone();
            lampObj.setLocalTranslation(pt);
            lampMap.put(lamp, lampObj);
            sceneManager.getWorldRoot().attachChild(lampObj);
            setLamp(lamp);
        }      
    }
    
    public void lampChanged(Lamp lamp){
        setLamp(lamp);
    }
    
    private void setLamp(final Lamp lamp){
        enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                
                AnimControl playerControl; // you need one Control per model
                Node player = (Node) assetManager.loadModel("Models/Sinbad/Sinbad.j3o"); // load a model
                playerControl = player.getControl(AnimControl.class); // get control over this model
                System.out.println(playerControl.getAnimationNames());
                
                // INSERT CODE HERE
//                if(lamp.isVisited()){
//                    Spatial lampObj = lampMap.get(lamp);
//                    AnimControl control =  sceneManager.getWorldRoot().getChild("Armature").getControl(AnimControl.class);
//                    System.out.println(control.getAnimationNames());
//                }else {
//                    
//                }
                
                return null;
            }
        });
    }
    
}
