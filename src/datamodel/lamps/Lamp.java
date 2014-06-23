/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel.lamps;

import com.jme3.math.Vector3f;

/**
 *
 * @author Admin
 */
public class Lamp {
    
    private String name;
    
    private double x;
    
    private double y;
    
    private double z;
    
    private int id;

    protected boolean visited = false;

    public Lamp(String name, double x, double y, double z, int id) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
    }
    
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        
        sb.append("Lampe ");
        sb.append(name);
        return sb.toString();
    }
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Vector3f getLocationVector(){
        return new Vector3f((float)x, (float)y, (float)z);
    }     
    
    public boolean isVisited() {
        return visited;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }
    
    

    public void setVisited(boolean visited) {
        System.out.println("visited");
        this.visited = visited;
        LampManager.getInstance().lampChanged(this);
        
    }
    
    
    
    
    
}
