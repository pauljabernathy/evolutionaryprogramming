/*
 * Individual.java
 *
 * Created on February 3, 2006, 4:23 PM
 */

package evolution.queens;

/**
 *
 * @author  Paul J. Abernathy
 */
public class Individual {
    
    private int[] chromosome;
    private int fitness;
    
    /** Creates a new instance of Individual */
    public Individual(int[] chromosome) {
        this.chromosome = chromosome;
    }
    
    protected int[] getChromosome() {
        return chromosome;
    }
    
    protected int getFitness() {
        return fitness;
    }
    
    protected int calculateFitness() {
        return 0;
    }
    
    /** For now we will have the Queens class tell the Individual what it's fitness is */
    protected void setFitness(int fitness) {
        this.fitness = fitness;
    }
    
}
