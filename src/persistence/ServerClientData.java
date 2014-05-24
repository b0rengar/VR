package persistence;

import java.util.HashMap;

/**
 * Speichert Informationen �ber aktive Clients
 * 
 */
public class ServerClientData {

    private static HashMap<Integer, ServerClientData> players = new HashMap<Integer, ServerClientData>();
    private long playerId;
    private boolean connected;

    public static synchronized void add(int id) {
        players.put(id, new ServerClientData());
    }

    public static synchronized void remove(int id) {
        players.remove(id);
    }

    public static synchronized boolean exsists(int id) {
        return players.containsKey(id);
    }

    public static synchronized boolean isConnected(int id) {
        return players.get(id).connected;
    }

    public static synchronized void setConnected(int id, boolean connected) {
        players.get(id).connected = connected;
    }

    public static synchronized long getPlayerId(int id) {
        return players.get(id).playerId;
    }

    public static synchronized void setPlayerId(int id, long playerId) {
        players.get(id).playerId = playerId;
    }

    public long getPlayerId() {
        return playerId;
    }

}
