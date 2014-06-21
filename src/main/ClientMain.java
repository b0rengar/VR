package main;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
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
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import config.Settings;
import config.Setup;

import controls.UserInputControl;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.textfield.TextFieldControl;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import listener.ClientNetListener;
import manager.SceneManager;
import manager.SyncManager;
import messages.ActionMessage;
import messages.ControlMessage;
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
import messages.StartGameMessage;
import messages.SyncCharacterMessage;
import messages.SyncRigidBodyMessage;
import persistence.Player;

/**
 * test
 * @author fibu
 */
public class ClientMain extends SimpleApplication implements ScreenController{
    Client myClient = null;
    private static ClientMain app;  
    
	private SceneManager sceneManager;
	private SyncManager syncManager;
	private Nifty nifty;
	private NiftyJmeDisplay niftyDisplay;
	private TextRenderer statusText;
	private NetworkClient client;
	private ClientNetListener clientNetListener;
	private BulletAppState bulletState;
	private BitmapText crossHair;
	private UserInputControl userInputControl;
        
        private Geometry mark;

    
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
        initKeys();
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
            ImageMessage.class,
            EntityActionMessage.class);
        stateManager.attach(syncManager);
        sceneManager = new SceneManager(this, rootNode);
//        sceneManager.addUserControl(new HUDControl(nifty.getScreen("hud")));
        userInputControl = new UserInputControl(inputManager, cam);
        sceneManager.addUserControl(userInputControl);
        stateManager.attach(sceneManager);
        syncManager.addObject(-1, sceneManager);
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
            if (userName.trim().length() == 0) {
                    setStatusText("Username invalid");
                    return;
            }
            clientNetListener.setName(userName);
            statusText.setText("Connecting..");
            try {
                    client.connectToServer(Settings.getInstance().getServer(), Settings.getInstance().getPort_tcp(), Settings.getInstance().getPort_udp());
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
                                    sceneManager.attachLevel();
                                    //sceneManager.addBox();
                                    statusText.setText("Fertig!");
                                    nifty.removeScreen("load_world");
                                    initCrossHair();
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
    }
        
    public void bind(Nifty nifty, Screen screen) {}
    public void onStartScreen() {}
    public void onEndScreen() {}
    
    @Override
    public void simpleUpdate(float tpf) {}

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
              if (results.size() > 0) {
                // The closest collision point is what was truly hit:
                CollisionResult closest = results.getCollision(1);
                // For each hit, we know distance, impact point, name of geometry.
                float dist = closest.getDistance();
                Vector3f pt = closest.getContactPoint();
                String hit = closest.getGeometry().getName();
                System.out.println("* Collision #");
                System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
                // Let's interact - we mark the hit with a red dot.
                mark.setLocalTranslation(closest.getContactPoint());
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
        
        /** Declaring the "Shoot" action and mapping to its triggers. */
        private void initKeys() {
          inputManager.addMapping("Shoot",
            new KeyTrigger(KeyInput.KEY_M)); // trigger 1: spacebar
//            new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 2: left-button click
          inputManager.addListener(actionListener2, "Shoot");
        }
}
