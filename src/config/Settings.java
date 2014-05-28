package config;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Settings {
	private static Settings instance = null;
	private String version;
	private int protocol_version;
	private String server;
	private int port_tcp;
	private int port_udp;
	private float netw_sync_freq;
	private float netw_max_physicsdelay;
	private int scene_fps;
	private int scene_resolution_width;
	private int scene_resolution_height;
	private float physics_fps;
	private boolean debug;
	private int character_speed;

	protected Settings(){
		try{
			InputStream file = new FileInputStream(new File("ress"+System.getProperty("file.separator")+"b0rengar.properties")) ;
			Properties props = new Properties();
			props.loadFromXML(file);
			version = props.getProperty("version");
			protocol_version = Integer.valueOf(props.getProperty("protocol_version"));
			server = props.getProperty("server");
			port_tcp = Integer.valueOf(props.getProperty("port_tcp"));
			port_udp = Integer.valueOf(props.getProperty("port_udp"));
                        
                        scene_resolution_width = Integer.valueOf(props.getProperty("scene_resolution_width"));
			scene_resolution_height = Integer.valueOf(props.getProperty("scene_resolution_height"));
			debug = new Boolean(props.getProperty("debug"));
                        netw_sync_freq = Float.valueOf(props.getProperty("netw_sync_freq"));
                        netw_max_physicsdelay = Float.valueOf(props.getProperty("netw_max_physicsdelay"));
			scene_fps = Integer.valueOf(props.getProperty("scene_fps"));
			physics_fps = Float.valueOf(props.getProperty("physics_fps"));
			character_speed = Integer.valueOf(props.getProperty("character_speed"));
                        
		} 
		catch(Exception ex){
			System.err.println("Error Load Settings: " + ex);
		}	 
	}

	public static Settings getInstance(){
		if (instance == null) instance = new Settings();
		return instance;
	}

	public String getVersion() {
		return version;
	}

	public String getServer() {
		return server;
	}

	public int getPort_tcp() {
		return port_tcp;
	}

	public int getPort_udp() {
		return port_udp;
	}

	public float getNetw_sync_freq() {
		return netw_sync_freq;
	}

	public float getNetw_max_physicsdelay() {
		return netw_max_physicsdelay;
	}

	

	public int getScene_resolution_width() {
		return scene_resolution_width;
	}

	public int getScene_resolution_height() {
		return scene_resolution_height;
	}

	public float getPhysics_fps() {
		return physics_fps;
	}

	public int getProtocol_version() {
		return protocol_version;
	}

	public boolean isDebug() {
		return debug;
	}

	public int getCharacter_speed() {
		return character_speed;
	}
}
