/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel.building;

import com.jme3.math.Vector3f;
import java.awt.Polygon;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 *
 * @author Chris
 */
public class H14 {
    
    public static void main(String[] args) {
        H14 h14 = new H14();
        System.out.println(h14.getAlpha());
        System.out.println(h14.getPoly().contains(-35, 0));
        
//        h14.rotatePolygon();
        
        Vector3f loc = new Vector3f(-33.84f, 0.0f, 12.0f);
        h14.getPlayerLocationOnMap(loc);
    }
    
    private Point2D.Double NW;
    private Point2D.Double NE;
    private Point2D.Double SW;
    private Point2D.Double SE;
    private double alpha = 0.0;
    private Polygon poly;
   
    private int mapWidth = 476;
    private int mapHeight = 173;
    
    public H14(){
        NW = new Point2D.Double(-42.05,-98.39);
        NE = new Point2D.Double(-4.95,-89.9);
        SW = new Point2D.Double(-70.05,5.95);
        SE = new Point2D.Double(-35.84,14.42);
        alpha = calcAlpha();
        poly = new Polygon();
        poly.addPoint((int)NW.x, (int)NW.y);
        poly.addPoint((int)NE.x, (int)NE.y);
        poly.addPoint((int)SE.x, (int)SE.y);
        poly.addPoint((int)SW.x, (int)SW.y);
    }
    
    private double calcAlpha(){
//        double a = Point2D.distance(NE.x, NE.y, SE.x, SE.y);
//        double b = Point2D.distance(NE.x, NE.y, 0, 0);
//        double c = Point2D.distance(0, 0, SE.x, SE.y);
//        return Math.toDegrees(Math.acos(((b*b)+(c*c)-(a*a))/(2*b*c)));
        
        double a = Point2D.distance(SW.x, SW.y, SW.x, SE.y);
        double b = Point2D.distance(SW.x, SW.y, SE.x, SE.y);
        double c = Point2D.distance(SW.x, SE.y, SE.x, SE.y);
        System.out.println("b = " + b);
//        return Math.toDegrees(Math.acos(((b*b)+(c*c)-(a*a))/(2*b*c)));
        return 14.0;
        
//        aplha = arccos( (b² + c² - a²) / 2bc )
    }
    
    public Point2D.Double getPlayerLocationOnMap(Vector3f location){
         double locX = location.x;
         double locY = location.z;
         locX -= SE.x;
         locY -= SE.y;
         
        double cosalpha = Math.cos(alpha/180.0*Math.PI);
        double sinalpha = Math.sin(alpha/180.0*Math.PI);

        locX = cosalpha*(locX)+sinalpha*(locY);
	locY = -sinalpha*(locX)+cosalpha*(locY);
        
        double yMap = mapHeight - ((locX * mapHeight)/-34.0);
        double xMap = mapWidth - ((locY * mapWidth)/-107.8);
        xMap -= 25;
        yMap += 10;
        if(yMap < 0)
            yMap = 0;
        if(yMap > mapHeight)
            yMap = mapHeight;
        if(xMap < 0)
            xMap = 0;
        if(xMap > mapWidth)
            xMap = mapWidth;
        
//        System.out.println("y = " + yMap + " --- x = " + xMap);
        
        return new Point2D.Double(xMap, yMap);
    }
    
    private void rotatePolygon(){
        double cosalpha = Math.cos(alpha/180.0*Math.PI);
        double sinalpha = Math.sin(alpha/180.0*Math.PI);
//        int halfwidth = 35/2, halfheight = 109/2;
        int halfwidth = 0, halfheight = 0;
        double i = 30.89, j = -104.32;
//        double i = -34.21, j = -8.47;
        double orgi = cosalpha*(i-halfheight)+sinalpha*(j-halfwidth)+halfheight;
	double orgj = -sinalpha*(i-halfheight)+cosalpha*(j-halfwidth)+halfwidth;
        System.out.println(orgi + " --- " + orgj);
    }
    
    public boolean playerInBuildung(Vector3f location){
        return poly.contains(location.x, location.z);
    }
    
    public Point2D.Double getNW() {
        return NW;
    }

    public void setNW(Point2D.Double NW) {
        this.NW = NW;
    }

    public Point2D.Double getNE() {
        return NE;
    }

    public void setNE(Point2D.Double NE) {
        this.NE = NE;
    }

    public Point2D.Double getSW() {
        return SW;
    }

    public void setSW(Point2D.Double SW) {
        this.SW = SW;
    }

    public Point2D.Double getSE() {
        return SE;
    }

    public void setSE(Point2D.Double SE) {
        this.SE = SE;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public Polygon getPoly() {
        return poly;
    }

    public void setPoly(Polygon poly) {
        this.poly = poly;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }
    
     
    
}
