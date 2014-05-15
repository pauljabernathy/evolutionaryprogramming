/*
 * Customer.java
 *
 * Created on March 30, 2006, 9:25 PM
 */

package evolution.router;

/**
 *
 * @author  Paul J. Abernathy
 */
public class Customer extends ShippingPoint {
    
    private int quantity_demanded;
    
    /** Creates a new instance of Customer */
    public Customer(int x, int y) {
        super(x, y);
        type = 1;
        quantity_demanded = (int)(Math.random() * 10);
    }
    
    /** Creates a new instance of Customer */
    public Customer(int x, int y, int id) {
        super(x, y, id);
        type = 1;
        quantity_demanded = (int)(Math.random() * 10);
    }
    
    protected int getQuantityDemanded() {
        return quantity_demanded;
    }
}
