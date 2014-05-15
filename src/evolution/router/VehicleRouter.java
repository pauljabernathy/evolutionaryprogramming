/*
 * VehicleRouter.java
 *
 * Created on March 30, 2006, 9:20 PM
 */

package evolution.router;
import evolution.router.*;

import evolution.*;
import java.util.Vector;

/**
 *
 * @author  Paul J. Abernathy
 */
public class VehicleRouter {
    
    //static parameters
    protected int num_suppliers;
    protected int num_customers;
    protected int num_shipping_points;
    protected int num_trucks;
    
    //state variables
    protected Individual[] population;
    protected ShippingPoint [] shipping_points;
    
    protected int individual_count;
    protected int generation_count;
    
    private int[] in_order;
    
    protected RouterGui rgui;
    
    /** Creates a new instance of VehicleRouter */
    public VehicleRouter() {
                
        /*population_size = 4;
        inversion_mutation_probability = 0.5;
        swap_mutation_probability = 0.5;*/
        num_suppliers = 4;
        num_customers = 16;
        num_trucks = 4;
        num_shipping_points = num_customers + num_suppliers;
        shipping_points = new ShippingPoint[num_shipping_points];
        
        
        individual_count = 0;
        generation_count = 0;
        
        //initializeShippingPoints();
        
        //population = new Individual[population_size];
        //initializePopulation();
    }
    
    /** Creates a new instance of VehicleRouter */
    public VehicleRouter(RouterGui rgui) {
        this.rgui = rgui;
        /*population_size = 4;
        inversion_mutation_probability = 0.5;
        swap_mutation_probability = 0.5;*/
        num_suppliers = 4;
        num_customers = 16;
        num_trucks = 4;
        num_shipping_points = num_customers + num_suppliers;
        shipping_points = new ShippingPoint[num_shipping_points];
        
        
        individual_count = 0;
        generation_count = 0;
        
        //initializeShippingPoints();
        
        //population = new Individual[population_size];
        //initializePopulation();
    }
    
    protected boolean containsGene(int[] chromosome, int gene) {
        
        for(int i = 0; i < chromosome.length; i++) {
            if(chromosome[i] == gene) {
                println(gene + " is present.", 1);
                return true;
            }
        }
        println(gene + " is not present.", 1);
        return false;
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
    
    
    
    /** displays the chromosome given
     * @param chromosome
     */
    protected void displayChromosome(int[] chromosome, int output_code) {
        int size = chromosome.length;
        
        for(int i = 0; i < size; i++) {
            print(Integer.toString(chromosome[i]) + " ", output_code);
        }
        println("", output_code);// "\tfitness:  " + evaluateFitness(chromosome), output_code);
    }
    
    /** displays the chromosome (genotype) of the Individual
     * @param individual the Individual to be displayed
     */
    protected void displayChromosome(Individual individual, int output_code) {
        int[] chromosome = individual.getChromosome();
        int size = chromosome.length;
        
        for(int i = 0; i < size; i++) {
            print(Integer.toString(chromosome[i]) + " ", output_code);
        }
        println("\tfitness:  " + Double.toString(individual.getFitness())  + "\t" + individual.getAllOrdersDelivered(), output_code);
    }
    
    
    /** displays the info on each ShippingPoint (Customer and Supplier) */
    protected void displayShippingPoints() {
        for(int i = 0; i < num_shipping_points; i++) {
            println(shipping_points[i].display(), 0);
        }
    }
    
    
    protected double evaluateFitness(Individual individual) {
        print("evaluteFitness() with " + num_trucks + " trucks of ", 3);
        //displayChromosome(Individual.getChromosome(), 3);
        
        int[] chromosome = individual.getChromosome();
        int size = chromosome.length;
        
        for(int i = 0; i < size; i++) {
            print(Integer.toString(chromosome[i]) + " ", 3);
        }
        println("\tfitness:  " + Double.toString(individual.getFitness())  + "\t" + individual.getAllOrdersDelivered(), 3);
        
        //int[] chromosome = individual.getChromosome();
        double length = 0.0;
        double fitness = 0.0;
        if(chromosome.length != num_shipping_points) {
            //throw an exception?
            return -32000;
        }
        double rise = 0;
        double run = 0;
        
        boolean all_packages_delivered = true;
        
        for(int truck = 0; truck < num_trucks; truck++) {
            //println("truck #" + Integer.toString(truck), 3);
            /**/int start_index = num_shipping_points * truck / num_trucks;
            int end_index = num_shipping_points * (truck + 1) / num_trucks - 1;
            
            //check to see if the firxt stop is a supplier; if not, not all packages will be delivered
            if(!(shipping_points[chromosome[start_index]].getClass().getName()).equals("evolution.router.Supplier")) {
                /*println("truck " + truck + "start index = " + start_index + " class = " + " shipping point = " + shipping_points[start_index].getID() +
                    shipping_points[chromosome[start_index]].getClass().getName() + " != evolution.router.Supplier", 3);*/
                all_packages_delivered = false;
            }
            else {
            /*println("truck " + truck + "start index = " + start_index + " class = " + " shipping point = " + shipping_points[start_index].getID() +
                shipping_points[chromosome[start_index]].getClass().getName() + " == evolution.router.Supplier", 3);*/
            }
            
            //first find distance from the origin to first shipping point
            rise = shipping_points[chromosome[start_index]].getY();
            run = shipping_points[chromosome[start_index]].getX();
            length = Math.sqrt(rise * rise + run * run);
            fitness += length;
            println("distance between origin and " + start_index + " = " + Double.toString(length), 4);
            
            /**/ //now find distance from each shipping point to the next
            for(int i = start_index + 1; i <= end_index; i++) {
                run = shipping_points[chromosome[i]].getX() - shipping_points[chromosome[i - 1]].getX();
                rise = shipping_points[chromosome[i]].getY() - shipping_points[chromosome[i - 1]].getY();
                length = Math.sqrt(rise * rise + run * run);
                fitness += length;
                println("distance between " + Integer.toString(i - 1) + " and " + Integer.toString(i) + " = " + Double.toString(length), 4);
                println("rise = " + Double.toString(rise) + "   run = " + Double.toString(run), 4);
            }
            /**/
            //now find length from last shipping point back to origin
            rise = shipping_points[chromosome[end_index]].getY();
            run = shipping_points[chromosome[end_index]].getX();
            length = Math.sqrt(rise * rise + run * run);
            fitness += length; /**/
            println("distance between " + Integer.toString(num_shipping_points - 1) + " and origin = " + Double.toString(length), 4);
            println("fitness = " + Double.toString(fitness), 4);
            //return fitness;/**/
        }
        println("fitness = " + Double.toString(fitness) + " all_packages_deliverd = " + all_packages_delivered, 3);
        individual.setFitness(fitness);
        individual.setAllOrdersDelivered(all_packages_delivered);
        return fitness;
    }
    
    protected double evaluateFitness(int[] chromosome) {
        return evaluateFitness(new Individual(chromosome));
    }
    
    /** initializes the population of Individuals */
    protected void initializePopulation() {/*
        population = new Individual[population_size];
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
        Individual lr = new Individual(long_route, this);
        
        //population[0] = lr;
        
        for(int t = 0; t < population_size; t++) {
            //population = new Individual[population_size];
            chromosome = createChromosome();
            /**population[t] = new Individual(chromosome, this);
            //population[t].setFitness((double)(evaluateFitness(chromosome)));/**
            //population[t] = new Individual(good, this);
            //population[t].setFitness((double)(evaluateFitness(g)));
            
            individual_count++;
        }
        //insertIntoPopulation(g);
        
        displayPopulation();
        //sortPopulation();
        //displayPopulation();/**/
    }
    
    
    
    /** outputs the message according to the code given
     * @param message
     * @param code
     */
    private void output(String message) {
        if(rgui != null) {
            rgui.output(message);
            //System.out.print(message);
        }
        else
            System.out.print(message);
        
        System.out.flush();
    }
    
    /** outputs the message according to the code given
     * @param message
     * @param code
     */
    protected void println(String message, int code) {
        switch(code) {
            case 0:
                output(message + "\n");
                break;
            case 1:
                //output(message + "\n");
                break;
            case 2:
                //System.out.print(message);
                break;
            case 3:
                //output(message + "\n");
                break;
            case 4:
                //output(message + "\n");
                break;
            case 5:
                //output(message + "n");
            default:
                //do nothing
        }
    }
    
    /** outputs the message according to the code given
     * @param message
     * @param code
     */
    protected void print(String message, int code) {
        switch(code) {
            case 0:
                output(message);
                break;
            case 1:
                //System.out.println(message);
                break;
            case 2:
                //System.out.print(message);
                break;
            case 3:
                //output(message);
                break;
            default:
                //do nothing
        }
    }
    
    
    protected void runSimulation(int population_size, int max_generations, double inversion_mutation_probability, double swap_mutation_probability) {
        /*this.population_size = population_size;
        this.max_generations = max_generations;
        this.inversion_mutation_probability = inversion_mutation_probability;
        this.swap_mutation_probability = swap_mutation_probability;
        
        population = new Individual[population_size];
        initializePopulation();
        int k = 0;
        for(; k < max_generations; k++) {
            //println(Integer.toString(k), 0);
            if(k % 5000 == 0) {
                println("After " + generation_count + " generations:", 0);
                displayPopulation();
            }
            try {
                doOneGeneration();
            }
            catch(SolutionFoundException sfe) {
                print(sfe.getMessage() + " in " + Integer.toString(k) + " generations and " + 
                    Integer.toString(individual_count) + " individuals.", 0);
                //k = max_generations;
                break;
                
            }
            //println(Integer.toString(k), 0);
            /**if(k % 10 == 0) {
                println("k % 10 == 0", 4);
                if(true) { //population[population_size / 2] == population[0]) {
                    int[] child = mutateScramble(population[0].getChromosome());
                    Individual n = new Individual(child);
                    n.setFitness(evaluateFitness(child));
                    insertForciblyIntoPopulation(n);
                }
            }/**
        }
        println("\n" + Integer.toString(k) + " generations and " + 
                    Integer.toString(individual_count) + " individuals", 0);
        println("swap mutation probability = " + swap_mutation_probability + "; inversion mutation probability = " + inversion_mutation_probability, 0);
        displayPopulation();*/
        
    }
    
    protected void runSimulation() {
        /*max_generations = 2000;
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
            }/**
        }
        print(Integer.toString(k) + " generations and " + 
                    Integer.toString(individual_count) + " individuals.", 0);
        displayPopulation();
        //println("population_size = " + population_size, 0);*/
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //VehicleRouter vr = new VehicleRouter();
        //vr.runSimulation();
        RouterGui rgui = new RouterGui();     
  }   
}
