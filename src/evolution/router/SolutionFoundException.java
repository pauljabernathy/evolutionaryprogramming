/*
 * SolutionFoundException.java
 *
 * Created on February 6, 2006, 5:59 AM
 */

package evolution.router;

/**
 *
 * @author  Paul J. Abernathy
 */
public class SolutionFoundException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>SolutionFoundException</code> without detail message.
     */
    public SolutionFoundException() {
        super("A solution has been found.");
    }
    
    
    /**
     * Constructs an instance of <code>SolutionFoundException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SolutionFoundException(String msg) {
        super(msg);
    }
}
