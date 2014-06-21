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
        
    }
    
    private Point2D.Double NW;
    private Point2D.Double NE;
    private Point2D.Double SW;
    private Point2D.Double SE;
    private double alpha = 0.0;
    private Polygon poly;
    
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
        double a = Point2D.distance(NE.x, NE.y, SE.x, SE.y);
        double b = Point2D.distance(NE.x, NE.y, 0, 0);
        double c = Point2D.distance(0, 0, SE.x, SE.y);
        return Math.toDegrees(Math.acos(((b*b)+(c*c)-(a*a))/(2*b*c)));
//        aplha = arccos( (b² + c² - a²) / 2bc )
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
    
     
    
}
