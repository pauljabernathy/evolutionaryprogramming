/*
 * ShippingPoint.java
 *
 * Created on March 30, 2006, 9:25 PM
 */

package evolution.router;

/**
 *
 * @author  Paul J. Abernathy
 */
public abstract class ShippingPoint {
    
    protected int type;
    protected int x;
    protected int y;
    protected int id;
    
    /** Creates a new instance of ShippingPoint */
    public ShippingPoint(int x, int y) {
        type = 0;
        this.x = x;
        this.y = y;
        id = 0;
    }
    
     /** Creates a new instance of ShippingPoint */
    public ShippingPoint(int x, int y, int id) {
        type = 0;
        this.x = x;
        this.y = y;
        this.id = id;
    }
    
    protected String display() {
        String str_type = "";
        if(type == 1) {
            str_type = "Customer";
        }
        else {
            str_type = "Supplier";
        }
        //System.out.println(str_type + "\tid = " + Integer.toString(id) + "\tx = " + Integer.toString(x) + "\ty= " + Integer.toString(y));
        return str_type + "\tid = " + Integer.toString(id) + "\tx = " + Integer.toString(x) + "\ty= " + Integer.toString(y);
    }
    
    public boolean isCustomer() {
        if(type == 1)
            return true;
        else
            return false;
    }
    
    public boolean isSupplier() {
        if(type == 2)
            return true;
        else
            return false;
    }
    
    protected int getX() {
        return x;
    }
    
    protected int getY() {
        return y;
    }
    
    protected int getID() {
        return id;
    }
    
    protected void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
