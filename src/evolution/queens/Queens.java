/*
 * Queens.java
 *
 * Created on January 29, 2006, 6:48 PM
 */

package evolution.queens;

import evolution.*;
import java.util.Vector;
//import java.Math.random;

/**
 *
 * @author  Paul J. Abernathy
 */
public class Queens {
    
    private Vector population;
    
    //parameter variables
    private int population_size;
    private int num_queens;
    private int num_generations;
    private double mutation_probability;
    private double crossover_probability;
    
    private int MAX_INDIVIDUAL_COUNT = 10000;
    
    private int individual_count;   //the number of individuals that have existed
    private int generation_count;   //the number of generations actually run, since when a solution is found it should exit
    
    private ChessBoard chessboard;
    
    /** Creates a new instance of Queens */
    public Queens() {
        population_size = 50;
        num_queens = 8;
        num_generations = 20;
        individual_count = 0;
        generation_count = 0;
        mutation_probability = 0.8;
        crossover_probability = 1.0;
        
        population = new Vector();
        initializePopulation();
    }
    
    public Queens(int population_size, int num_queens, int max_individuals, double mutation_probability, double crossover_probability, ChessBoard chessboard) {
        this.population_size = population_size;
        this.num_generations = 20;
        this.MAX_INDIVIDUAL_COUNT = max_individuals;
        this.num_queens = num_queens;
        this.mutation_probability = mutation_probability;
        this.crossover_probability = crossover_probability;
        this.chessboard = chessboard;
        
        this.individual_count = 0;
        this.generation_count = 0;
        population = new Vector();
        
        initializePopulation();
    }
    
    /** @deprected
     * I think
     */
    protected int evaluatePhenotype(int[] dna) {
        return 0;
    }
    
    private void initializePopulation() {
        /**/for(int i = 0; i < population_size; i++) {
            int[] chromosome = makeChromosome();
            
            Individual individual = new Individual(chromosome);
            individual.setFitness(collisions(chromosome));
            population.add(individual);
            individual_count++;
        }
        generation_count = 1;
    }
    
    private int collisions(int[] chromosome) {
        //chromosome represents an individual chessboard
        
        int difference = 0;
        int num_collisions = 0;
        int row;
        for(int i = 0; i < num_queens; i++) {
            for(int j = 0; j < num_queens; j++) {
                difference = i - j;
                if(i != j)
                {
                    if(chromosome[j] == chromosome[i] - difference) {
                        num_collisions++;
                    }
                    if(chromosome[j] == chromosome[i] + difference) {
                        num_collisions++;
                    }
                }   
            }
        }
        return num_collisions;
    }
    
    private boolean containsGene(int[] chromosome, int gene) {
        
        for(int i = 0; i < chromosome.length; i++) {
            if(chromosome[i] == gene) {
                return true;
            }
        }
        
        return false;
    }
    
    /** Displays the entire population, one Individual per line of output */
    protected void displayPopulation() {
        
        System.out.println("\nPopulation is:");
        for(int i = 0; i < population.size(); i++) {
            Individual individual = (Individual)population.get(i);
            displayIndividual(individual);
        }
    }
    
    /** Displays the chromosome and fitness for one individual */
    private void displayIndividual(Individual individual) {
        int[] c = individual.getChromosome();
        for(int j = 0; j < c.length; j++)
        {
            output(Integer.toString(c[j]) + " ");
        }
        int fitness = individual.getFitness();
        output(":    collisions = " + Integer.toString(fitness) + " \n");
    }
    
    /** Carries out one generation - reproduction and insertion into population 
     * (which bumps out previous members of the population).
     */
    private void doOneGeneration() throws SolutionFoundException {
        generation_count++;
        for(int i = 0; i < population_size - 1; i++) {
            Individual first = (Individual)population.get((int)(Math.random() * population_size));
            Individual second = (Individual)population.get((int)(Math.random() * population_size));

            if(second.getFitness() < first.getFitness()) {
                Individual temp = first;
                first = second;
                second = temp;
            }
            for(int j = 0; j < 3; j++) {
                Individual next = (Individual)population.get((int)(Math.random() * population_size));

                if(next.getFitness() < first.getFitness()) {
                    first = next;
                }
                else if(next.getFitness() < second.getFitness()) {
                    second = next;
                }
            }
            mate(first, second);
        }
    }
    
    /**Inserts a new child into the population, searching throught the popualation and
     * replacing the first one it finds with a worse or equal fitness
     * @param Inidividual new_child
     * @return void
     */
    private void insertIntoPopulation(Individual new_child) {
        int child_fitness = new_child.getFitness();
        for(int i = 0; i < population.size(); i++) {
            Individual to_replace = (Individual)population.get(i);
            if(to_replace.getFitness() > child_fitness) {
                //using ">" because right now fitness is just the # of collicions, so really it's a measure of unfitness
                population.remove(i);
                population.insertElementAt(new_child, i);
                return;
            }
        }
    }
    
    /** Creates a chromosome for an individual; no value is repeated in the array */
    private int[] makeChromosome() {
        int[] chromosome = new int[num_queens];
        
        //initialize the chromosome to -1
        for(int i = 0; i < num_queens; i++) {
            chromosome[i] = -1;
        }
        
        int gene = (int)(Math.random() * num_queens);
        chromosome[0] = gene;
        
        int sum = gene;
        for(int k = 1; k < num_queens - 1; k++) {
            gene = (int)(Math.random() * num_queens);
            
            while(containsGene(chromosome, gene)) {
                gene = (int)(Math.random() * num_queens);
            }
            chromosome[k] = gene;
            sum += gene;
        }

        int max_sum = /**6; // /**/ 28;
        chromosome[num_queens - 1] = max_sum - sum;
        return chromosome;
    }
    
    /** "Mates" the two individuals to form two new children and place them in the population if they are fit enough.
     */
    protected void mate(Individual mother, Individual father) throws SolutionFoundException {
        int[] mom = mother.getChromosome();
        int[] dad = father.getChromosome();
        int[] child1 = new int[num_queens]; 
        int[] child2 = new int[num_queens]; 
        individual_count += 2;
        
        if(individual_count >= MAX_INDIVIDUAL_COUNT) {
            throw new SolutionFoundException("Maximum number of individuals reached:  " + Integer.toString(individual_count));
        }
        
        int breakpoint = (int)(Math.random() * num_queens);
        
        //Crossing over only happens with probabiity crossover_probability, so we use a random number to
        //see if it should happen.  If not, we can prevent it from happening by setting the breakpoint to 
        //num_queens - 1.
        if(Math.random() > crossover_probability) {
            breakpoint = num_queens - 1;
        }
        
        for(int i = 0; i < num_queens; i++) {
            if(i < breakpoint) {
                child1[i] = mom[i];
                child2[i] = dad[i];
            }
            else {
                child1[i] = -1;
                child2[i] = -1;
            }
        }    
        
        int j = breakpoint; //index on other chromosome

        for(int i = breakpoint; i < num_queens; i++) {

            while(containsGene(child2, mom[j])) {
                j++;
                if(j == num_queens) {
                    j = 0;
                }
            }
            child2[i] = mom[j];
            j++;
            if(j == num_queens) {
               j = 0;
            }

        }
        
        j = breakpoint;
        for(int i = breakpoint; i < num_queens; i++) {

            while(containsGene(child1, dad[j])) {
                j++;
                
                if(j == num_queens) {
                    j = 0;
                }
            }
            child1[i] = dad[j];
            j++;
            if(j == num_queens) {
               j = 0;
            }

        }

        Individual i1 = new Individual(child1);
        Individual i2 = new Individual(child2);
        i1.setFitness(collisions(child1));
        i2.setFitness(collisions(child2));
        
        if(Math.random() < mutation_probability) {
            child1 = mutateChild(child1);
            i1.setFitness(collisions(child1));
        }
        
        if(Math.random() < mutation_probability) {
            child2 = mutateChild(child2);
            i2.setFitness(collisions(child2));
        }
        
        
        insertIntoPopulation(i1);
        insertIntoPopulation(i2);
        
        if(i1.getFitness() == 0 || i2.getFitness() == 0) {
            throw new SolutionFoundException();
        }
    }
    
    /** makes a mutation in the int[] child by swapping two random elements */
    private int[] mutateChild(int[] child) {
        int first = (int)(Math.random() * num_queens);
        int second = (int)(Math.random() * num_queens);
        //make sure first and second are different
        while(first == second) {
            second = (int)(Math.random() * num_queens);
        }
        int temp = child[first];
        child[first] = child[second];
        child[second] = temp;
        
        return child;
    }
    
    /** Sends the String message to the ChessBoard to be added to the end of the current line of output 
     * If there is no ChessBoard, it sends it to System.out.print()
     */
    private void output(String message) {
        if(chessboard != null) {
            chessboard.output(message);
        }
        else {
            System.out.print(message);
        }
    }
    
    /** Sends the String message to the ChessBoard to bea new line of utput 
     * If there is no ChessBoard, it sends it to System.out.println()
     */
    protected void outputLine(String message) {
        if(chessboard != null) {
            chessboard.output(message + "\n");
        }
        else {
            System.out.println(message);
        }
    }
    
    /** Runs the simulation
     */
    protected void runSimulation() {

        outputLine("The Eight Queens Program.");
        
        sortPopulation();
        try {
            while(individual_count < MAX_INDIVIDUAL_COUNT) {
                doOneGeneration();
            }
        }
        catch(SolutionFoundException sfe) { outputLine(sfe.getMessage()); }

        outputLine("total number of individuals in history:  " + Integer.toString(individual_count));
        outputLine("Number of generations done:  " + Integer.toString(generation_count));
        outputLine("Solution:");
        displayIndividual((Individual)population.get(0));
        
        outputLine("");
    }
    
    /** Does an Insertion Sort on the population by fitness */
    private void sortPopulation() {
       
        population_size = population.size();
        for(int i = 1; i < population_size; i++) {
            
            //current is the one we're going to put in the right place
            Individual current = (Individual)(population.get(i));
            int current_fitness = current.getFitness();
            
            //Test to see if current's fitness is better than the previous one; if so, then it's out of order
            int last_fitness = ((Individual)population.get(i - 1)).getFitness();
            
            if(current_fitness < last_fitness) {
                //Now, search from the beginning to see where it goes
                int j = 0;
                while(current_fitness >= ((Individual)population.get(j)).getFitness() &&  j < population_size - 1) {
                    j++;
                }
                //We've found the correct place
                population.removeElementAt(i);
                population.insertElementAt(current, j);
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("The Eight Queens Program");
        Queens queens = new Queens();
        queens.runSimulation();
    }
}
