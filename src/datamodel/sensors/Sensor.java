/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel.sensors;

/**
 *
 * @author Admin
 */
public class Sensor {
    
    private SensorGroup group;
    private int id;
    private String description;
    private String type;
    private String floor;
    private String room;
    private String positionDescription;
    
    private double x;
    private double y;
    private double z;

    public Sensor(int id) {
        this.id = id;
    }

    public Sensor(SensorGroup group, int id, String description, String type, String floor, String room, String positionDescription, double x, double y, double z) {
        this.group = group;
        this.id = id;
        this.description = description;
        this.type = type;
        this.floor = floor;
        this.room = room;
        this.positionDescription = positionDescription;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Sensor ");
        sb.append(group.getId());
        sb.append("/");
        sb.append(id);
        sb.append("\n");
        sb.append(floor);
        sb.append("\t");
        sb.append(room);
        sb.append("\n");
        sb.append("X:");
        sb.append(x);
        sb.append("\tY:");
        sb.append(y);
        sb.append("\tZ:");
        sb.append(z);
                
        
        return sb.toString();
    }
    
    
    /**
     * @return the group
     */
    public SensorGroup getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(SensorGroup group) {
        this.group = group;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the floor
     */
    public String getFloor() {
        return floor;
    }

    /**
     * @param floor the floor to set
     */
    public void setFloor(String floor) {
        this.floor = floor;
    }

    /**
     * @return the room
     */
    public String getRoom() {
        return room;
    }

    /**
     * @param room the room to set
     */
    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * @return the positionDescription
     */
    public String getPositionDescription() {
        return positionDescription;
    }

    /**
     * @param positionDescription the positionDescription to set
     */
    public void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return the z
     */
    public double getZ() {
        return z;
    }

    /**
     * @param z the z to set
     */
    public void setZ(double z) {
        this.z = z;
    }
    
   
    
    
    
}
