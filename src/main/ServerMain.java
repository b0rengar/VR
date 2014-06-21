package main;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import config.Settings;
import config.Setup;
import datamodel.sensors.SensorManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import listener.ServerNetListener;
import manager.GameManager;
import manager.SceneManager;
import manager.SyncManager;
import messages.ActionMessage;
import messages.ControlMessage;

/**
 * test
 * @author fibu
 */
public class ServerMain extends SimpleApplication {
    private static ServerMain app = new ServerMain();
    private static Server server;

  public static void main(String[] args) {
    
      new SensorManager();
    AppSettings settings = new AppSettings(true);
    settings.setRenderer(null);
    settings.setAudioRenderer(null);
    Setup.registerSerializers();
    Setup.setLogLevels(Settings.getInstance().isDebug());
    app = new ServerMain();
    app.setShowSettings(false);
    app.setPauseOnLostFocus(false);
    app.setSettings(settings);
    app.start();  // headless type for servers!
 }
    private SceneManager worldManager;
    private GameManager gameManager;
    private SyncManager syncManager;
    private BulletAppState bulletState;
    
    
    @Override
    public void simpleInitApp() {
        try {
            server = Network.createServer(Settings.getInstance().getPort_tcp(), Settings.getInstance().getPort_udp());
            server.start();
        } catch (IOException ex) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, "Cannot start server: {0}", ex);
            return;
        }
        bulletState = new BulletAppState();
        getStateManager().attach(bulletState);
        bulletState.getPhysicsSpace().setAccuracy(Settings.getInstance().getPhysics_fps());
        //create sync manager
        syncManager = new SyncManager(app, server);
        syncManager.setSyncFrequency(Settings.getInstance().getNetw_sync_freq());
        syncManager.setMessageTypes(ActionMessage.class, ControlMessage.class);
        stateManager.attach(syncManager);
        //cerate world manager
        worldManager = new SceneManager(this, rootNode);
        stateManager.attach(worldManager);
        //register world manager with sync manager so that messages can apply their data
        syncManager.addObject(-1, worldManager);
        //create server side game manager
        gameManager = new GameManager();
        stateManager.attach(gameManager);
        new ServerNetListener(this, server, worldManager, gameManager);
        Logger.getLogger(ServerNetListener.class.getName()).log(Level.INFO, "Server bereit!");        
    }

    @Override
    public void simpleUpdate(float tpf) {}

    @Override
    public void simpleRender(RenderManager rm) {}
    
    @Override
    public void destroy() {
        super.destroy();
        server.close();
    }
    
}
