/*
 * Supplier.java
 *
 * Created on March 30, 2006, 9:26 PM
 */

package evolution.router;

/**
 *
 * @author  Paul J. Abernathy
 */
public class Supplier extends ShippingPoint {
    
    /** Creates a new instance of Supplier */
    public Supplier(int x, int y) {
        super(x, y);
        type = 2;
    }
    
    /** Creates a new instance of Supplier */
    public Supplier(int x, int y, int id) {
        super(x, y, id);
        type = 2;
    }
    
}
