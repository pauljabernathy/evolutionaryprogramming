/*
 * EvolutionaryVehicleRouter.java
 *
 * Created on April 30, 2006, 5:14 PM
 */

package evolution.router;
import evolution.router.*;
//import evolution.SolutionFoundException;
import java.util.Vector;
import java.util.Date;

/**
 *
 * @author  Paul J. Abernathy
 */
public class EvolutionaryVehicleRouter extends VehicleRouter {
    
    private Individual[] population;
    private boolean scramble_mutation;
    
    protected int population_size;
    protected int max_generations;
    
    protected double swap_mutation_probability;
    protected double inversion_mutation_probability;
    
    private int average_generations;
    private int average_individuals;
    private double average_time;
    
    private boolean eight_shipping_points;
    protected int num_runs;
    
    /** Creates a new instance of EvolutionaryVehicleRouter */
    public EvolutionaryVehicleRouter() {
        super();
        initializeShippingPoints();
        individual_count = 0;
        generation_count = 0;
    }
    
    public EvolutionaryVehicleRouter(RouterGui rgui) {
        //super(rgui);
        
        this.rgui = rgui;
        population_size = 4;
        inversion_mutation_probability = 0.5;
        swap_mutation_probability = 0.5;
        num_suppliers = 4;
        num_customers = 16;
        num_trucks = 4;
        scramble_mutation = false;
        
        num_shipping_points = num_customers + num_suppliers;
        shipping_points = new ShippingPoint[num_shipping_points];
             
        individual_count = 0;
        generation_count = 0;
        
        eight_shipping_points = true;
        
        if(eight_shipping_points) {
            num_shipping_points = 8;
            num_trucks = 2;
        }
        
    }
    
    private int[] createChromosome() {
        println("createChromosome()", 1);
        int[] chromosome = new int[num_shipping_points];
        for(int i = 0; i < num_shipping_points; i++) {
            chromosome[i] = i;
        }
        
        int p = 0;
        for(int q = 0; q < num_shipping_points; q++) {
            //if(Math.random() < 0.8) {
                int temp = chromosome[q];
                p = (int)(Math.random() * num_shipping_points);
                chromosome[q] = chromosome[p];
                chromosome[p] = temp;
            //}
        }
        
        return chromosome;
    }
    
    /** performs crossover on the two Individuals given and produces two offspring
     * @param mother
     * @param father
     * @return Vector
     */
    private Vector crossover(Individual mother, Individual father) {
        
        //don't want to do crossover on two chromosomes that are equal
        if(mother.equals(father)) {
            Vector to_return = new Vector();
            to_return.add(mother);
            to_return.add(father);
            return to_return;
        }
        
        println("crossover", 1);
        int[] mom = mother.getChromosome();
        int[] dad = father.getChromosome();
        int[] child1 = new int[num_shipping_points]; 
        int[] child2 = new int[num_shipping_points]; 
        
        println("mother is", 1);
        displayChromosome(mother, 1);
        println("father is", 1);
        displayChromosome(father, 1);
        
        int breakpoint = (int)(Math.random() * num_shipping_points) + 3;
        println("breakpoint = " + breakpoint, 1);
        
        int comparisons = 0;
        
        for(int i = 0; i < num_shipping_points; i++) {
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

        for(int i = breakpoint; i < num_shipping_points; i++) {

            while(containsGene(child2, mom[j])) {
                j++;
                comparisons++;
                if(comparisons > 30) {
                    println("leaving", 1);
                    System.exit(0);
                }
                
                if(j == num_shipping_points) {
                    j = 0;
                }
                if(j == breakpoint) //not supposed to happen, but it does
                    break;
            }
            child2[i] = mom[j];
            j++;
            if(j == num_shipping_points) {
               j = 0;
            }

        }
        
        j = breakpoint;
        comparisons = 0; 
        for(int i = breakpoint; i < num_shipping_points; i++) {

            while(containsGene(child1, dad[j])) {
                j++;
                comparisons++;
                if(comparisons > 30) {
                    println("leaving", 1);
                    System.exit(0);
                }
                if(j == num_shipping_points) {
                    j = 0;
                }
                if(j == breakpoint)
                    break;
            }
            child1[i] = dad[j];
            j++;
            if(j == num_shipping_points) {
               j = 0;
            }
        }
        
        Vector v = new Vector();
        Individual i1 = new Individual(child1, this);
        Individual i2 = new Individual(child2, this);
        //i1.setFitness(evaluateFitness(i1));
        //i2.setFitness(evaluateFitness(i1));
        v.add(i1);
        v.add(i2);
        println("kid1:",1);
        displayChromosome(i1, 1);
        println("kid2:", 1);
        displayChromosome(i2, 1);
        println("returning from crossover",1);
        return v;
    }
    
    /** displays the chromosome (genotype) of the Individual
     * @param individual the Individual to be displayed
     */
    protected void displayChromosome(Individual individual, int output_code) {
        if(individual == null) {
            //System.exit(0);
        }
        int[] chromosome = individual.getChromosome();
        //double fitness = 
        //individual.setChromosome(new int[] { 0, 1 } );
        Individual infr = individual;
        double fitness = individual.getFitness();
        int size = chromosome.length;
        
        for(int i = 0; i < size; i++) {
            print(Integer.toString(chromosome[i]) + " ", output_code);
        }
        println("\tfitness:  " + Double.toString(individual.getFitness())  + "\t" + individual.getAllOrdersDelivered(), output_code);
    }
    
    /** displays the Individuals in the population one at a time */
    protected void displayPopulation() {
        
        /**/println("\nDisplaying population:", 0);
        for(int i = 0; i < population_size; i++) {
            println("i = " + i, 1);
            if(population[i] == null) {
                //System.exit(0);
            }
            displayChromosome(population[i], 0);
        }
    }
    
    private void doOneGeneration() throws SolutionFoundException {
        //crossover(population[0], population[1]);
        println("doOneGeneration()", 1);
        for(int i = 0; i < population_size; i++) {
            int ma = (int)(Math.random() * population_size);
            int pa = (int)(Math.random() * population_size);
            mate(population[ma], population[pa]);
        }
        generation_count++;
        println("returning from doOneGeneration()", 1);
    }
    
    protected void initializePopulation() {
        //super.initializePopulation()
        this.population = new Individual[population_size];
        println("population size = " + population_size, 0);
        int[] chromosome = new int[num_shipping_points];
        
        int[] almost_perfect = {1, 17, 18, 19, 0, 6, 5, 4, 3, 2, 11, 10, 9, 8, 7, 16, 15, 14, 13, 12 }; //{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 };
        
        int[] good = { 0, 1, 2, 5, 6, 3, 8, 4, 7, 17, 18, 19, 9, 10, 11, 12, 16, 14, 15, 13 };
        good = new int[] { 0, 1, 17, 18, 19, 5, 5, 4, 3, 2, 10, 11, 9, 8, 7, 15, 16, 14, 12, 11 };
        int[] perfect = { 16, 15, 14, 13, 12, 0, 1, 17, 18, 19, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 };
        //Individual p = new Individual(perfect);
        //p.setFitness(evaluateFitness(p));
        //Individual g = new Individual(good);
        //g.setFitness(evaluateFitness(g));
        //Individual a = new Individual(almost_perfect);
        //insertIntoPopulation(p);
        
        int[] long_route = { 0, 11, 6, 17, 8, 5, 16, 3, 9, 14, 15, 7, 2, 12, 18, 10, 19, 13, 1, 4 };
        //Individual lr = new Individual(long_route, this);
        
        //population[0] = lr;
        //population_size = 10;
        for(int t = 0; t < population_size; t++) {
            //population = new Individual[population_size];
            chromosome = createChromosome();
            /**/population[t] = new Individual(chromosome, this);
            //population[t].setFitness((double)(evaluateFitness(chromosome)));/**/
            //population[t] = new Individual(good, this);
            //population[t].setFitness((double)(evaluateFitness(g)));
            println("t = " + t, 1);
            if(population[t] == null) {
                System.exit(0);
            }
            
            individual_count++;
        }
        //insertIntoPopulation(g);
        
        for(int p = 0; p < population_size; p++) {
             println("p = " + p, 1);
            if(population[p] == null) {
                System.exit(0);
            }
        }
        
        //displayPopulation();
        //sortPopulation();
        //displayPopulation();
    }
    
    /** Initializes the Customers and Suppliers and creates their positions on the "map"
     */
    private void initializeShippingPoints() {
        shipping_points = new ShippingPoint[num_shipping_points];
        
        int x_size = 8;
        int y_size = 8;
        int[][] grid = new int[x_size][y_size];     //keeps track of which locations are taken
        //so no two ShippingPoints have the same location
        
        int x = 0;
        int y = 0;
        
        for(int i = 0; i < num_shipping_points; i++)
        {
            x = (int)(Math.random() * x_size);
            y = (int)(Math.random() * y_size);
            while(grid[x][y] != 0) {
                x = (int)(Math.random() * x_size);
                y = (int)(Math.random() * y_size);
            }
            grid[x][y] = 1;
            if(i % 4 == 0) {    //be careful -- probably won't work in the general case
                Supplier supplier = new Supplier(x, y, i);
                shipping_points[i] = supplier;
            }
            else {
                Customer customer = new Customer(x, y, i);
                shipping_points[i] = customer;
            }
        }
        
        if(eight_shipping_points) {
        //for 8 shipping points
        shipping_points[0].setPosition(4, 0);
        shipping_points[1].setPosition(8, 0);
        shipping_points[2].setPosition(8, 4);
        shipping_points[3].setPosition(8, 8);
        shipping_points[4].setPosition(4, 8);
        shipping_points[5].setPosition(0, 8);
        shipping_points[6].setPosition(0, 4);
        shipping_points[7].setPosition(0, 0);
        }
        
        else {
        //for 20 shipping points
        /**/shipping_points[0].setPosition(4,0);
        shipping_points[1].setPosition(8, 0);
        shipping_points[2].setPosition(12, 0);
        shipping_points[3].setPosition(16, 0);
        shipping_points[4].setPosition(20, 0);
        shipping_points[5].setPosition(20, 4);
        shipping_points[6].setPosition(20, 8);
        shipping_points[7].setPosition(20, 12);
        shipping_points[8].setPosition(20, 16);
        shipping_points[9].setPosition(20, 20);
        shipping_points[10].setPosition(16, 20);
        shipping_points[11].setPosition(12, 20);
        shipping_points[12].setPosition(8, 20);
        shipping_points[13].setPosition(4, 20);
        shipping_points[14].setPosition(0, 20);
        shipping_points[15].setPosition(0, 16);
        shipping_points[16].setPosition(0, 12);
        shipping_points[17].setPosition(0, 8);
        shipping_points[18].setPosition(0, 4);
        shipping_points[19].setPosition(0, 0);/**/
        }
        
        displayShippingPoints();
    }
    
    /** inserts new_child into population even if all members of the population are better;
     * used to increase the diversity of the population when many Individuals are the same
     * so that it doesn't converge on a local optimum
     * @param new_child the Individual to insert
     * @return the index of population[] where new_child was put; returns population_size if it was not inserted at all
     */
    private int insertForciblyIntoPopulation(Individual new_child) {
        int position = insertIntoPopulation(new_child);
        if( position == population_size) {
            population[population_size - 1] = new_child;
            position = population_size - 1;
        }
        return position;
    }
    
    /**inserts the new gene into the population by replacing the best Individual worse than new_child
     * Say the fitness is 203.77 and population[] is { 189.04, 201.5, 207.3, 219.21 }, after the method it will be
     * { 189.04, 201.5, 203.77, 219.21 }
     * @param new_child the Individual to insert
     * @return the index of population[] where new_child was put; returns population_size if it was not inserted at all
     */
    private int insertIntoPopulation(Individual new_child) {
        println("insertIntoPopulation(Individual new_child)", 3);
        displayChromosome(new_child, 3);
        double child_fitness = new_child.getFitness();
        for(int i = 0; i < population_size; i++) {
            Individual to_replace = population[i];
            //if(to_replace.getFitness() > child_fitness) {
            if(new_child.isLessThan(to_replace)) {
                //using ">" because right now fitness is just the # of collicions, so really it's a measure of unfitness
                print("\nreplacing ", 3);
                displayChromosome(to_replace, 3);
                population[i] = new_child;
                return i;
            }
        }
        return population_size;
    }
    
    private void mate(Individual mama, Individual papa) throws SolutionFoundException {
        println("mate", 1);
        Vector litter = crossover(mama, papa);
        individual_count += 2;
        Individual kid1 = (Individual)litter.get(0);
        Individual kid2 = (Individual)litter.get(1);
        
        int[] chromosome = new int[kid1.getChromosome().length];
        for(int i = 0; i < chromosome.length; i++) {
            chromosome[i] = kid1.getChromosome()[i];
        }
        if(Math.random() < swap_mutation_probability) {
            //println("Before mutation:", 1);
            //displayChromosome(chromosome);
            chromosome = mutateSwapChild(chromosome);
        }
        if(Math.random() < inversion_mutation_probability) {
           chromosome = mutateInversionChild(chromosome);
            //kid1 = new Individual(chromosome);
            //println("After mutation", 1);
            //displayChromosome(kid1.getChromosome());
        }
        kid1 = new Individual(chromosome, this);
        //kid1.setChromosome(chromosome);
        //kid1.setFitness(evaluateFitness(kid1));
        
        int[] chromosome2 = new int[kid2.getChromosome().length];
        for(int i = 0; i < chromosome2.length; i++) {
            chromosome2[i] = kid2.getChromosome()[i];
        }
        if(Math.random() < swap_mutation_probability) {
            //println("Before mutation:", 3);
            //displayChromosome(chromosome);
            chromosome2 = mutateSwapChild(chromosome2);
        }
        if(Math.random() < inversion_mutation_probability) {
            //println("Before mutation:", 2);
            chromosome2 = mutateInversionChild(chromosome2);
            //kid2 = new Individual(chromosome);
            //println("After mutation", 2);
            //displayChromosome(kid2.getChromosome(), 1);
        }
        kid2 = new Individual(chromosome2, this);
        //kid2.setChromosome(chromosome);
        //kid2.setFitness(evaluateFitness(kid2));
            
        insertIntoPopulation(kid1);
        insertIntoPopulation(kid2);
        
        if(kid1.containsChromosome(new int[] { 0, 1, 2, 3, 4, 5, 6, 7 }) || kid2.containsChromosome(new int[] { 0, 1, 2, 3, 4, 5, 6, 7 }) ) {
            throw new SolutionFoundException("Solution found");
        }
        
        if(kid1.isOptimal() || kid2.isOptimal()) {
            throw new SolutionFoundException("Solution found");
        }
        
        /*if(generation_count % 1000 == 0) {
            if(population[population_size / 2] == population[0]) {
                int[] child = mutateScramble(kid1.getChromosome());
            }
        }*/
        
        println("returning from mate()", 1);
    }
        
    /** makes a mutation in the int[] child by swapping two random elements */
    private int[] mutateChild(int[] child) {
        println("mutateChild()", 1);
        int first = (int)(Math.random() * num_shipping_points);
        int second = (int)(Math.random() * num_shipping_points);
        //make sure first and second are different
        while(first == second) {
            second = (int)(Math.random() * num_shipping_points);
        }
        int temp = child[first];
        child[first] = child[second];
        child[second] = temp;
        
        return child;
    }
    
    /** performs inverse mutation on the given chromosome, finding two random points
     * and flipping the order of everything between those two points
     * @param child
     * @return child
     */
    private int[] mutateInversionChild(int[] child) {
        println("before inversion mutation:",1);
        displayChromosome(child, 1);
        int first = (int)(Math.random() * num_shipping_points);
        int second = (int)(Math.random() * num_shipping_points);
        
        //make sure first and second are different
        while(first == second) {
            second = (int)(Math.random() * num_shipping_points);
        }
        
        //Now make sure front < back.
        int front, back = 0;
        if(first < second) {
            front = first;
            back = second;
        }
        else {
            back = first;
            front = second;
        }
        //front = 5;
        //back = 10;
        
        //output("\nchild before mutation:", 2);
        //displayChromosome(child);
        //Perform the inversion.
        while(front < back) {
            int temp = child[front];
            child[front] = child[back];
            child[back] = temp;
            front++;
            back--;
        }
        //output("\nchild after mutation:", 2);
        //displayChromosome(child);
        
        println("after inversion mutation:",1);
        displayChromosome(child, 1);
        return child;
    }
    
    /** returns a heavily mutated child; used to increase diverstiy
     * @param child
     * @return child
     */
    private int[] mutateScramble(int[] child) {
        println("Doing a scramble mutation", 4);
        displayChromosome(child, 4);
        for(int r = 0; r < 3; r++) {
            child = mutateSwapChild(child);
            child = mutateInversionChild(child);
        }
        displayChromosome(child, 4);
        return child;
    }
    
    /** makes a mutation in the int[] child by swapping two random elements */
    private int[] mutateSwapChild(int[] child) {
        println("before mutateSwapChild", 1);
        displayChromosome(child, 1);
        int first = (int)(Math.random() * num_shipping_points);
        int second = (int)(Math.random() * num_shipping_points);
        //make sure first and second are different
        while(first == second) {
            second = (int)(Math.random() * num_shipping_points);
        }
        int temp = child[first];
        child[first] = child[second];
        child[second] = temp;
        
        println("after mutateSwapChild", 1);
        displayChromosome(child, 1);
        return child;
    }
    
    /** Does an Insertion Sort on the population by fitness */
    private void sortPopulation() {
        println("sortPopulation()", 1);
        //population_size = population.size();
        for(int i = 1; i < population_size; i++) {
            
            //current is the one we're going to put in the right place
            Individual current = population[i];
            double current_fitness = current.getFitness();
            
            //Test to see if current's fitness is better than the previous one; if so, then it's out of order
            double last_fitness = population[i - 1].getFitness();
            
            //if(current_fitness < last_fitness) {
            if(population[i].isLessThan(population[i - 1])) {
                
                println(population[i].getFitness() + " < " + population[i - 1].getFitness(), 5);
                //Now, search from the beginning to see where it goes
                int j = 0;
                //System.out.print("j = " + Integer.toString(j) + "   ");
                //while(current_fitness >= population[j].getFitness() &&  j < population_size - 1) {
                while(population[j].isLessThanOrEqual(population[i]) &&  j < population_size - 1) {
                    j++;
                    //System.out.print("j = " + Integer.toString(j) + "   ");
                }
                //We've found the correct place
                Individual temp = new Individual();
                temp = population[i];
                for(int q = i; q > j; q--) {
                    population[q] = population[q - 1];
                    //System.out.print("q = " + Integer.toString(q) + "   ");
                }
                population[j] = temp;
            }
        }
    }
    
    protected void doOneRun() {
        
        initializePopulation();
        
        generation_count = 0;
        individual_count = 0;
        int k = 0;
        for(; k < max_generations; k++) {
            //println(Integer.toString(k), 0);
            /*if(k % 5000 == 0) {
                println("After " + generation_count + " generations:", 0);
                displayPopulation();
            }*/
            try {
                doOneGeneration();
            }
            catch(SolutionFoundException sfe) {
                print(sfe.getMessage() + " in " + Integer.toString(k) + " generations and " + 
                    Integer.toString(individual_count) + " individuals.", 0);
                displayPopulation();
                //k = max_generations;
                break;
                
            }
            //println(Integer.toString(k), 0);
            /**/if(scramble_mutation && k % 10 == 0) {
                println("k % 10 == 0", 4);
                if(true) { //population[population_size / 2] == population[0]) {
                    int[] child = mutateScramble(population[0].getChromosome());
                    Individual n = new Individual(child);
                    n.setFitness(evaluateFitness(child));
                    insertForciblyIntoPopulation(n);
                }
            }/**/
            average_generations += generation_count;
            average_individuals += individual_count;
        }
        println("\n" + Integer.toString(k) + " generations and " + 
                    Integer.toString(individual_count) + " individuals", 0);
        
        println("max generations = " + max_generations + "; swap mutation probability = " + swap_mutation_probability + 
                "; inversion mutation probability = " + inversion_mutation_probability + "; scramble = " + scramble_mutation  +
                "population size = " + population_size, 0);
        displayPopulation();
    }
    
    protected void runSimulation(int population_size, int max_generations, double inversion_mutation_probability, double swap_mutation_probability) {
        this.population_size = population_size;
        this.max_generations = max_generations;
        this.inversion_mutation_probability = inversion_mutation_probability;
        this.swap_mutation_probability = swap_mutation_probability;
        
        generation_count = 0;
        individual_count = 0;
        average_individuals = 0;
        average_generations = 0;
        
        //Date start_time = new Date();
        initializeShippingPoints();
        population = new Individual[population_size];
        initializePopulation();
        
        int num_good_solutions = 0;
        int num_great_solutions = 0;
        double best_fitness = 0.0;
        double ave_fitness = 0.0;
        
        double time = 0.0;
        num_runs = 20;
        for(int i = 0; i < num_runs; i++) {
            Date start_time = new Date();
            doOneRun();        
            Date stop_time = new Date();
            time += stop_time.getTime() - start_time.getTime();
            
            best_fitness = population[0].getFitness();
            if(best_fitness < 200.0 && population[0].getAllOrdersDelivered()) {
                num_great_solutions++;
                num_good_solutions++;
            }
            else if(best_fitness < 220.0 && population[0].getAllOrdersDelivered()) {
                num_good_solutions++;
            }
            ave_fitness += best_fitness;
            print("Best chromosome of that run was ", 0);
            displayChromosome(population[0], 0);
        }
        
        println("\n\nmax generations = " + max_generations + "; swap mutation probability = " + swap_mutation_probability + 
                "; inversion mutation probability = " + inversion_mutation_probability, 0);
        println("population size = " + population_size  + "; scramble = " + scramble_mutation, 0);
        
        println("average running time over " + num_runs + " runs = " + time / (double)num_runs, 0);
        /**println("average # of generations = " + (double)average_generations / (double)num_runs + 
                "\taverage # of individuals = " + (double)average_individuals / (double)num_runs, 0);/**/
        println("number of good solutions ( < 220.0) = " + num_good_solutions, 0);
        println("number of great solutions ( < 200.0) = " + num_great_solutions, 0);
        println("average best fitness = " + ave_fitness / (double)num_runs, 0);
    }
    
    protected void runSimulation() {
        initializeShippingPoints();
        initializePopulation();
        //super.runSimulation();
        
        max_generations = 2000;
        int k = 0;
        for(k = 0; k < max_generations; k++) {
            try {
                doOneGeneration();
            }
            catch(SolutionFoundException sfe) {
                print(sfe.getMessage() + " in " + Integer.toString(k) + " generations and " + 
                    Integer.toString(individual_count) + " individuals.", 0);
                //k = max_generations;
                break;
                
            }
            /**if(k % 100 == 0) {
                if(population[population_size / 2] == population[0]) {
                    int[] child = mutateScramble(population[0].getChromosome());
                    population[0] = new Individual(child);
                    population[0].setFitness(evaluateFitness(population[0]));
                }
            }/**/
        }
        print(Integer.toString(k) + " generations and " + 
                    Integer.toString(individual_count) + " individuals.", 0);
        displayPopulation();
        //println("population_size = " + population_size, 0);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RouterGui rgui = new RouterGui();
    }
}
