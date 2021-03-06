package persistence;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import com.jme3.math.Vector3f;

public class Player {

	private static HashMap<Long, Player> players = new HashMap<Long, Player>();
	private long id;
	private String name;
        private int O2;
        private int pulse;
        private Vector3f location;
	private long character_entity_id = -1;
	private long entity_id = -1;
	private int group_id = -1;

	public static synchronized Player getPlayer(long id) {
		if (!players.containsKey(id)) return null;
		return players.get(id);
	}

	public static synchronized List<Player> getPlayers() {
		LinkedList<Player> list = new LinkedList<Player>(players.values());
		return list;
	}

	public static synchronized long getNew(String name) {
		long id = 0;
		while (players.containsKey(id)) {
			id++;
		}
		players.put(id, new Player(id, name));
		return id;
	}

	public static synchronized void add(long id, Player player) {
		players.put(id, player);
	}

	public static synchronized void remove(long id) {
		players.remove(id);
	}

	public Player(long id) {
		this.id = id;
	}

	public Player(long id, String name) {
		this.id = id;
		this.name=name;
		this.entity_id = -1l;
	}

	public Player(long id, int groupId, String name) {
		this.id = id;
		this.group_id = groupId;
		this.name=name;
		this.entity_id = -1l;
		this.character_entity_id = -1l;
	}

        public Player(long id, int groupId, String name, int o2) {
		this.id = id;
		this.group_id = groupId;
		this.name=name;
		this.entity_id = -1l;
		this.character_entity_id = -1l;
                this.O2 = o2;
	}
        
        public Player(long id, int groupId, String name, int o2, int pulse) {
		this.id = id;
		this.group_id = groupId;
		this.name=name;
		this.entity_id = -1l;
		this.character_entity_id = -1l;
                this.O2 = o2;
                this.pulse = pulse;
	}
        
	public long getId() {
		return id;
	}

	public long getCharacter_entity_id() {
		return character_entity_id;
	}

	public void setCharacter_entity_id(long character_entity_id) {
		this.character_entity_id = character_entity_id;
	}

	public long getEntity_id() {
		return entity_id;
	}

	public void setEntity_id(long entity_id) {
		this.entity_id = entity_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGroup_id() {
		return group_id;
	}

	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}
        
        public int getO2(){
            return O2;
        }
        
        public void setO2(int o2){
                this.O2 = o2;
        }

        public int getPulse() {
            return pulse;
        }

        public void setPulse(int pulse) {
            this.pulse = pulse;
        }

        public Vector3f getLocation() {
            return location;
        }

        public void setLocation(Vector3f location) {
            this.location = location;
        }
}
