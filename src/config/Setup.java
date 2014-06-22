package config;

import com.jme3.network.serializing.Serializer;
import java.util.logging.Level;
import java.util.logging.Logger;
import messages.ActionMessage;
import messages.ClientJoinMessage;
import messages.ControlMessage;
import messages.HandshakeMessage;
import messages.ServerAddEntityMessage;
import messages.ServerAddPlayerMessage;
import messages.SyncCharacterMessage;
import messages.ServerEnableEntityMessage;
import messages.ServerEnterEntityMessage;
import messages.ServerDisableEntityMessage;
import messages.ServerEntityDataMessage;
import messages.ServerJoinMessage;
import messages.SyncRigidBodyMessage;
import messages.ServerPlayerDataMessage;
import messages.ServerRemoveEntityMessage;
import messages.ServerRemovePlayerMessage;
import messages.StartGameMessage;
import messages.EntityActionMessage;
import messages.SensorChangeMessage;
import messages.ClientUserDataMessage;

public class Setup {

    public static void setLogLevels(boolean debug) {
        if (debug) {
            Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE);
            Logger.getLogger("org.lwjgl").setLevel(Level.WARNING);
            Logger.getLogger("com.jme3").setLevel(Level.FINEST);
            Logger.getLogger("main").setLevel(Level.FINEST);

        } else {
            Logger.getLogger("de.lessvoid").setLevel(Level.WARNING);
            Logger.getLogger("org.lwjgl").setLevel(Level.WARNING);
            Logger.getLogger("com.jme3").setLevel(Level.WARNING);
            Logger.getLogger("th_walk").setLevel(Level.WARNING);
        }
    }

    public static void registerSerializers() {
        Serializer.registerClass(ActionMessage.class);
        Serializer.registerClass(ClientJoinMessage.class);
        Serializer.registerClass(HandshakeMessage.class);
        Serializer.registerClass(ControlMessage.class);
        Serializer.registerClass(ServerAddEntityMessage.class);
        Serializer.registerClass(ServerAddPlayerMessage.class);
        Serializer.registerClass(SyncCharacterMessage.class);
        Serializer.registerClass(ServerEnableEntityMessage.class);
        Serializer.registerClass(ServerDisableEntityMessage.class);
        Serializer.registerClass(ServerEnterEntityMessage.class);
        Serializer.registerClass(ServerEntityDataMessage.class);
        Serializer.registerClass(ServerJoinMessage.class);
        Serializer.registerClass(SyncRigidBodyMessage.class);
        Serializer.registerClass(ServerPlayerDataMessage.class);
        Serializer.registerClass(ServerRemoveEntityMessage.class);
        Serializer.registerClass(ServerRemovePlayerMessage.class);
        Serializer.registerClass(StartGameMessage.class);
        Serializer.registerClass(EntityActionMessage.class);
        Serializer.registerClass(SensorChangeMessage.class);
        Serializer.registerClass(ClientUserDataMessage.class);
    }

}
