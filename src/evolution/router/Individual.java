/*
 * Individual.java
 *
 * Created on April 2, 2006, 2:36 PM
 */

package evolution.router;
import evolution.router.*;

/**
 *
 * @author  Paul J. Abernathy
 */
public class Individual {
    
    private int[] chromosome;
    private double fitness;
    private boolean all_orders_delivered;
    
    /** Creates a new instance of Individual */
    public Individual() {
    }
    
    /** Creates a new instance of Individual */
    public Individual(int[] chromosome) {
        this.chromosome = chromosome;
        all_orders_delivered = false;
    }
    
    public Individual(int[] chromosome, VehicleRouter router) {
        this(chromosome);
        setFitness(router.evaluateFitness(this));
    }
    
    protected int[] getChromosome() {
        return chromosome;
    }
    
    protected void setChromosome(int[] chromosome) {
        this.chromosome = chromosome;
    }
    
    protected double getFitness() {
        return fitness;
    }
    
    /** For now we will have the VehicleRouter class tell the Individual what it's fitness is */
    protected int calculateFitness() {
        return 0;
    }
    
    /** For now we will have the VehicleRouter class tell the Individual what it's fitness is */
    protected void setFitness(double fitness) {
        this.fitness = fitness;
    }
    
    protected boolean getAllOrdersDelivered() {
        return all_orders_delivered;
    }
    
    protected void setAllOrdersDelivered(boolean all_orders_delivered) {
        this.all_orders_delivered = all_orders_delivered;
    }
    
    /** tells if the Individual is less than other
     *The caveat is that we are defining a lower fitness to be better
     */
    protected boolean isLessThan(Individual other) {
       /** System.out.println("fitness = " + fitness + "\torders delivered " + all_orders_delivered +
            "\tother's fitness = " + other.getFitness() + "\tother's orders delivered " + other.getAllOrdersDelivered());/**/
        if(!all_orders_delivered && !other.getAllOrdersDelivered()) {
            //neither Individual has all orders delivered; the most likely case
            if(other.getFitness() <= fitness)
                return false;
            else
                return true;
        }
        else if(!all_orders_delivered && other.getAllOrdersDelivered()) {
            //System.out.println("I didn't deliver all my orders and the other guy did.  Returning false.");
            return false;
            //return true;
        }
        else if(all_orders_delivered && !other.getAllOrdersDelivered()) {
            //System.out.println("I delivered all my orders and the other guy didn't.  Returning true.");
            return true;
            //return false;
        }
        else {
            //all orders delivered in both Individuals
            if(other.getFitness() <= fitness)
                return false;
            else
                return true;
        }
    }
    
    protected boolean equals(Individual other) {
        int[] genes = other.getChromosome();
        
        for(int y = 0; y < chromosome.length; y++) {
            if(genes[y] != chromosome[y])
                return false;
        }
        return true;
        /*if(all_orders_delivered == other.getAllOrdersDelivered() && fitness == other.getFitness())
            return true;
        else
            return false;*/
    }
    
    protected boolean isLessThanOrEqual(Individual other) {
        if(isLessThan(other) || equals(other))
            return true;
        else
            return false;
    }
    
    protected boolean containsChromosome(int[] c) {
        if(chromosome.length != c.length)
            return false;
        
        for(int i = 0; i < c.length; i++) {
            if(chromosome[i] != c[i])
                return false;
        }
        return true;
    }
    
    protected boolean isOptimal() {
        if(all_orders_delivered && fitness == 48.25798040898392)
            return true;
        else
            return false;
    }
    
}
