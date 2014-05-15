/*
 * HeuristicVehicleRouter.java
 *
 * Created on April 29, 2006, 1:52 PM
 */

package evolution.router;
import java.util.Date;

/**
 *
 * @author  Paul J. Abernathy
 */
public class HeuristicVehicleRouter extends VehicleRouter {
    private int num_permutations;
    private Individual best;
    private int[] best_chromosome;
    private int[] original;
    
    /** Creates a new instance of HeuristicVehicleRouter */
    public HeuristicVehicleRouter() {
        num_permutations = 0;
        best_chromosome = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
        original = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
        best = new Individual(best_chromosome, this);
        num_shipping_points = 8;
        num_trucks = 2;
        initializeShippingPoints();
    }
    
    public HeuristicVehicleRouter(RouterGui rgui) {
        //this();
        num_permutations = 0;
        best_chromosome = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
        original = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
        best = new Individual(best_chromosome, this);
        num_shipping_points = 8;
        num_trucks = 2;
        this.rgui = rgui;
         
        initializeShippingPoints();
        num_trucks = 2;
        //displayBest();
    }
    
    protected Individual doHeuristicSearch(int num_shipping_points) {
        int[] chromosome = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 };
        Individual indy = new Individual(chromosome);
        return indy;
    }
    
    private void doHeuristicSearch(int[] chromosome, int start_index) {
        //System.out.println(chromosome.toString());
        //displayChromosome(chromosome, 0);
        
        int length = chromosome.length;
        for(int i = 0; i < length; i++) {
            //System.out.println("i = " + i + " start_index = " + start_index);//  + "  j = " + j);
            if(!containsGene(chromosome, i, start_index)) {
                chromosome[start_index] = i;
                if(start_index < length - 1)
                    doHeuristicSearch(chromosome, start_index + 1);
                else {
                    //Find the one number used yet and put it in the last element
                    for(int k = 0; k < length; k++) {
                        if(!containsGene(chromosome, k, length - 1)) {
                            chromosome[length - 1] = k;
                        }
                    }
                    //System.out.print("One possible chromosome is ");
                    //displayChromosome(chromosome, 0);
                    Individual indy = new Individual(chromosome, this);
                    //displayChromosome(indy, 0);
                    if(indy.isLessThan(best)) {
                        int[] c = new int[chromosome.length];
                        for(int l = 0; l < chromosome.length; l++) {
                            c[l] = chromosome[l];
                        }
                        /*best = new Individual(c, this);
                        println("new best chromosome:", 0);
                        displayChromosome(best, 0);
                        displayBest();*/
                        setBest(c);
                    }
                    /**if(evaluateFitness(best_chromosome) < evaluateFitness(chromosome)) {
                        best_chromosome = chromosome;
                        displayBest();
                    }/**/
                    
                    num_permutations++;
                }
            }
        }
    }
    
    protected boolean containsGene(int[] chromosome, int gene, int end_index) {
        
        for(int i = 0; i < end_index; i++) {
            if(chromosome[i] == gene) {
                //System.out.println(gene + " is present.");
                return true;
            }
        }
        //System.out.println(gene + " is not present.");
        return false;
    }
    
    protected void displayBest() {
        best.setFitness(evaluateFitness(best));
        println("Best chromosome: ", 0);
        displayChromosome(best, 0);
        //displayChromosome(best_chromosome, 0);
        //System.exit(0);
        //System.out.println(num_trucks + " trucks");
    }
    
    /** displays the chromosome given
     * @param chromosome
     */
    protected void displayChromosome(int[] chromosome, int output_code) {
        int size = chromosome.length;
        
        for(int i = 0; i < size; i++) {
            //System.out.print(Integer.toString(chromosome[i]) + " ");
            print(Integer.toString(chromosome[i]) + " ", 0);
        }
        //System.out.println("\tfitness:  " + evaluateFitness(chromosome));//, output_code);
        println("\tfitness:  " + evaluateFitness(chromosome), 0);
    }
    
    /** Initializes the Customers and Suppliers and creates their positions on the "map"
     */
    private void initializeShippingPoints() {
        
        int x_size = 20;
        int y_size = 20;
        int[][] grid = new int[20][20];     //keeps track of which locations are taken
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
            //if(i % 4 == 1) {    //be careful -- probably won't work in the general case
            if(i == 2 || i == 6) {
                Supplier supplier = new Supplier(x, y, i);
                shipping_points[i] = supplier;
            }
            else {
                Customer customer = new Customer(x, y, i);
                shipping_points[i] = customer;
            }
        }
        shipping_points[0].setPosition(4, 0);
        shipping_points[1].setPosition(8, 0);
        shipping_points[2].setPosition(8, 4);
        shipping_points[3].setPosition(8, 8);
        shipping_points[4].setPosition(4, 8);
        shipping_points[5].setPosition(0, 8);
        shipping_points[6].setPosition(0, 4);
        shipping_points[7].setPosition(0, 0);
        
        displayShippingPoints();
    }
    
    protected int getNumPermutations() {
        return num_permutations;
    }
    
    private void setBest(int[] new_best) {
        /*println("changing best from", 0);
        displayChromosome(best, 0);
        println("to", 0);
        displayChromosome(new_best, 0);*/
        
        int[] c = new int[new_best.length];
        for(int i = 0; i < new_best.length; i++) {
            c[i] = new_best[i];
        }
        //best.setChromosome(c);
        //best.setFitness(evaluateFitness(best));
        best = new Individual(c, this);
        displayBest();
    }
 
    protected void runSimulation() {
        double time = 0.0;
        int num_runs = 1;
        for(int i = 0; i < num_runs; i++) {
            Date start_time = new Date();
            //doHeuristicSearch(best.getChromosome(), 0);
            doHeuristicSearch(original, 0);
            Date stop_time = new Date();
            time += stop_time.getTime() - start_time.getTime();
            println("i = " + i, 0);
            displayBest();
        }
        displayBest();
        println("average running time over " + num_runs + " runs = " + time / (double)num_runs, 0);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RouterGui rgui = new RouterGui();
    }   
    
}
