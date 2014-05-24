package messages;

import manager.SceneManager;
import com.jme3.network.serializing.Serializable;

@Serializable()
public class ImageMessage extends SyncMessage{
    private long entity_id;
    private byte[] image_data;
    private int image_id;
    
    public ImageMessage() { }

    public ImageMessage(long entity_id, int image_id, byte[] image_data) {
        this.setSyncId(-1);
        this.entity_id = entity_id;
        this.image_id = image_id;
        this.image_data=image_data;
    }

    public byte[] getImageData() {
		return image_data;
	}
    
    

	public int getImage_id() {
		return image_id;
	}

	@Override
    public void applyData(Object object) {
        SceneManager sceneManager = (SceneManager) object;
       sceneManager.setNextImage(entity_id, image_data);
    }

	public long getEntity_id() {
		return entity_id;
	}
	
	

}



