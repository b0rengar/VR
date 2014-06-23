package controls;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 * wird f�r die Animation des Characters ben�tigt
 *
 */
@SuppressWarnings("deprecation")
public class CharacterAnimControl implements Control {
	
    protected Spatial spatial;
    protected AnimControl animControl;
    protected CharacterControl characterControl;
    
    protected AnimChannel upperBodyAnimChannel; //f�r den Oberk�rper
    protected AnimChannel lowerBodyAnimChannel; //f�r den Unterk�rper

    public CharacterAnimControl() { }

    public void setSpatial(Spatial spatial) {
        if (spatial == null) return;
//        animControl = spatial.getControl(AnimControl.class);
        animControl = ((Node)spatial).getChild("Mesh_0583-ogremesh").getControl(AnimControl.class);
        characterControl = spatial.getControl(CharacterControl.class);
        if (animControl != null && characterControl != null) {
            upperBodyAnimChannel = animControl.createChannel();
            lowerBodyAnimChannel = animControl.createChannel();
            upperBodyAnimChannel.setLoopMode(LoopMode.Loop);
            lowerBodyAnimChannel.setLoopMode(LoopMode.Loop);
        }
    }

    public void update(float tpf) {
    	//wenn der Character sich in der Luft befindet..
//        if(!characterControl.onGround()){
//            if(!"JumpLoop".equals(upperBodyAnimChannel.getAnimationName())&&!"RunTop".equals(upperBodyAnimChannel.getAnimationName())) upperBodyAnimChannel.setAnim("JumpLoop");
//            if(!"JumpLoop".equals(lowerBodyAnimChannel.getAnimationName())&&!"RunBase".equals(lowerBodyAnimChannel.getAnimationName())) lowerBodyAnimChannel.setAnim("JumpLoop");
//            return;
//        }

        //wenn der Character sich bewegt
        if (characterControl.getWalkDirection().length() > 0) {
            if(!"walk".equals(upperBodyAnimChannel.getAnimationName()))
                upperBodyAnimChannel.setAnim("walk");
//            if(!"RunBase".equals(lowerBodyAnimChannel.getAnimationName()))
//                lowerBodyAnimChannel.setAnim("RunBase");
        }
//        else{
//            if(!"IdleTop".equals(upperBodyAnimChannel.getAnimationName()))
//                upperBodyAnimChannel.setAnim("IdleTop");
//            if(!"IdleBase".equals(lowerBodyAnimChannel.getAnimationName()))
//                lowerBodyAnimChannel.setAnim("IdleBase");
//        }
    }

    public void render(RenderManager rm, ViewPort vp) { }

    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void write(JmeExporter ex) throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void read(JmeImporter im) throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }
    
}
