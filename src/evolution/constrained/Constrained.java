/*
 * Constrained.java
 *
 * Created on March 1, 2006, 4:38 PM
 */

package evolution.constrained;
import java.lang.Math.*;
import evolution.SolutionFoundException;

/**
 *
 * @author  Paul J. Abernathy
 */
public class Constrained {
    
    private double[][] population;
    private double[][] ES_population;
    private int population_size;
    private int ES_population_size;
    
    private int num_generations;    //deprecated; replaced with MAX_GENERATION_COUNT
    private int MAX_GENERATION_COUNT;
    private int generation_count;
    private int individual_count;
    private int MAX_INDIVIDUAL_COUNT;
    
    private double alpha;
    private double sigma;
    private double mutation_probability;
    private double crossover_probability;
    private double boundary_mutation_probability;
    private double uniform_mutation_probability;
    private double nonuniform_mutation_probability;
    
    private boolean verbose;
    private boolean individual_termination;
    
    private ConstrainedGUI cgui;
    
    private int ave_genetic_generations;
    private int ave_ES_generations;
    private int ave_genetic_individuals;
    private int ave_ES_individuals;
    private double percent_genetic_solution_found;
    private double percent_ES_solution_found;
    private double ave_genetic_min_x1_value;
    private double ave_genetic_min_x2_value;
    private double ave_genetic_min_x3_value;
    private double ave_genetic_min_x4_value;
    private double ave_ES_min_x1_value;
    private double ave_ES_min_x2_value;
    private double ave_genetic_fitness;
    private double ave_ES_fitness;
    
    /** Creates a new instance of Constrained */
    public Constrained() {
        
        alpha = 0.5;
        sigma = 0.2;
        mutation_probability = 1.0;
        crossover_probability = 1.0;
        boundary_mutation_probability = 0.4;
        uniform_mutation_probability = 0.3;
        nonuniform_mutation_probability = 0.3;
        
        individual_count = 0;
        MAX_INDIVIDUAL_COUNT = 10000;
        
        num_generations = 500;
        MAX_GENERATION_COUNT = num_generations;
        population_size = 50;
        population = new double[population_size][5];
        
        ave_genetic_generations = 0;
        ave_ES_generations = 0;
        ave_genetic_individuals = 0;
        ave_ES_individuals = 0;
        
        percent_genetic_solution_found = 0.0;
        ave_genetic_min_x1_value = 0.0;
        ave_genetic_min_x2_value = 0.0;
        ave_genetic_min_x3_value = 0.0;
        ave_genetic_min_x4_value = 0.0;
        ave_genetic_fitness = 0.0;
        
        verbose = true;
        individual_termination = false;
        
    }
    
    public Constrained(ConstrainedGUI cgui) {
        this();
        this.cgui = cgui;
    }
    
    private double[] checkConstraints(double[] child) {
        
        //If x4, x2, or x3 have changed, we may need to cascade the change to the others whose constraints depend on it
         if(child[1] > 4.0 - 2.0 * child[3] || child[1] < 0.0) {
             child[1] = randomX2(child);
         }
         if(child[2] < 0.0 || child[2] > Math.min(4.0 - child[1] / 3.0, child[1] / 3.0)) {
            child[2] = randomX3(child);
         }
         if(child[0] != evaluateX1(child)) {
            child[0] = evaluateX1(child); //chopDigits((child[1] / 3.0) - child[2]);
         }
         child[4] = chopDigits(evaluateFitness(child), 5);
         
         return child;
    }
    
    protected double chopDigits(double value) {
        return chopDigits(value, 5);
    }
    
    protected double chopDigits(double value, int num_digits) {
        double power_of_ten = 100000.0; //Math.pow(10.0, (double)num_digits);
        //raising a number to a power seems to be real slow
        if(num_digits == 4)
            power_of_ten = 10000.0;
        
        power_of_ten = Math.pow(10.0, num_digits);
        double big_value = value * power_of_ten;
        int big_int = (int)(big_value * 10);
        
        if(big_int % 10 >= 5) {
            if(value > 0.0) {
                big_int += 10;
            }
            else {
                big_int -= 10;  //if value is negative then round down
            }
            big_int = big_int / 10;
            value = (double)(big_int) / power_of_ten;

            return value;
        }
        return (double)((int)(big_value)) / power_of_ten;
    }
    
    private double[] crossover(double[] mom, double[] dad) {
        int size = mom.length;
        double[] child = new double[size];    //length should be 2
        for(int i = 0; i < size - 1; i++) {
            alpha = Math.random();
            child[i] = chopDigits(alpha * mom[i] + (1.0 - alpha) * dad[i]);
        }
        child[0] = chopDigits((child[1] / 3.0) - child[2]);
        child[size - 1] = chopDigits(evaluateFitness(child));
        return child;
    }
    
    private double[] createChromosome() {
        double chromosome[] = new double[5];
        
        /**The constraints are: 
         *0<= x_4 <= 1
         *0 <= x_3 < 4 - 2x_4
         *0 <= x_3 <= min(4 - x_2/3, x_2/3)
         *x_1 = x_2/3 - x_3
         */
        chromosome[3] = chopDigits(Math.random());
        chromosome[1] = chopDigits(Math.random() * (4 - 2 * chromosome[3]));
        chromosome[2] = chopDigits(Math.random() * Math.min(4 - chromosome[1] / 3.0, chromosome[1] / 3.0));
        chromosome[0] = chopDigits((chromosome[1] / 3.0) - chromosome[2]);
        
        chromosome[4] = chopDigits(evaluateFitness(chromosome), 5);
        return chromosome;
    }
    
    private void createGeneticPopulation() {      
        int value = 0;
        double[] chromosome = new double[2];
        for(int i = 0; i < population_size; i++) {
            population[i] = createChromosome();//new double[] {x1, x2, chopDigits(evaluateFitness(x1, x2)) };
            individual_count++;
        }
        
        sortPopulation();
    }
    
    private void displayChromosome(double[] chromosome, boolean verbose_override) {
        outputLine("x1 = " + Double.toString(chromosome[0]) + "\t x2 = " + Double.toString(chromosome[1]) + 
            "\t x3 = " + Double.toString(chromosome[2]) + "\t x4 = " + Double.toString(chromosome[3]) +
            "\t fitness = " + Double.toString(chromosome[4]), verbose_override);
    }
    
    private void displayPopulation() {
        outputLine("", false);
        outputLine("Displaying Population.", false);
        double gene = 1.0;
        double gene2 = 1.0;
        for(int i = 0; i < population_size; i++) {
            displayChromosome(population[i], false);
        } 
    }
    
    private void doOneGeneration() throws SolutionFoundException {
        generation_count++;
        for(int r = 0; r < population_size - 1; r++) {
            double[] child = mate(population[r], population[r + 1]);
        }
        if(generation_count >= MAX_GENERATION_COUNT && !individual_termination) {
            throw new SolutionFoundException("Maximum number of generations done.");
        }
    }
    
     private double evaluateFitness(double[] chromosome) {
        double x1 = chromosome[0];
        double x2 = chromosome[1];
        double x3 = chromosome[2];
        double x4 = chromosome[3];
        return Math.pow(x1, 0.6) + Math.pow(x2, 0.6) - 6 * x1 - 4 * x3 + 3 * x4;
    }
     
     private double evaluateX1(double[] chromosome) {
         return chopDigits((chromosome[1] / 3.0) - chromosome[2]);
     }
     
     private void insertIntoPopulation(double[] child) {
        for(int i = 0; i < population_size; i++) {
            if(population[i][4] > child[4]) {
                population[i] = child;
                i = population_size;
            }
        }
    }
     
     private double[] mate(double[] mom, double[] dad) throws SolutionFoundException {
        double[] child = mom;
        
        if(Math.random() <= crossover_probability)
            crossover(mom, dad);
        
        individual_count++;
        
        if(Math.random() < mutation_probability) {
            
            if(Math.random() < boundary_mutation_probability) {
                child = mutateBoundary(child);
            }
            if(Math.random() * (double)(MAX_GENERATION_COUNT - generation_count) / ((double)MAX_GENERATION_COUNT) < uniform_mutation_probability) {
                child = mutateUniform(child);
            }
            if(Math.random() * (double)(generation_count) / ((double)MAX_GENERATION_COUNT) < nonuniform_mutation_probability) {
                child = mutateNonUniform(child);
            }
        }
        
        if(child[4] == -4.5142 || child[4] == -4.51418) { // || child[4] ==  -4.51423) {
            insertIntoPopulation(child);
            throw new SolutionFoundException("Minimum found");
        }
        /**/if(individual_count >= MAX_INDIVIDUAL_COUNT && individual_termination) {
            insertIntoPopulation(child);
            throw new SolutionFoundException("Maximum number of individuals created.");
        }/**/
           
        insertIntoPopulation(child);
        return child;
    }
     
     private double[] mutateUniform(double[] child) {
         int index = (int)(3.0 * Math.random()) + 1;
         
         switch(index) {
             case(0):
                 break;
             case(3):
                 child[3] = chopDigits(Math.random());
                 break;
             case(1):
                 child[1] = chopDigits(Math.random() * (4.0 - 2.0 * child[3]));
                 break;
             case(2):
                 child[2] = chopDigits(Math.random() * Math.min(4.0 - child[1] / 3.0, child[1] / 3.0));
                 break;
         }
         
         //If x4, x2, or x3 have changed, we may need to cascade the change to the others whose constraints depend on it
         child = checkConstraints(child);
         return child;
     }
     
     private double[] mutateNonUniform(double[] child) {
         
         int index = (int)(3 * Math.random()) + 1;
         boolean right_side = true;
         if(Math.random() < 0.5)
             right_side = false;
         
         double t = ((double)(MAX_GENERATION_COUNT - generation_count)) / ((double)MAX_GENERATION_COUNT);
         
         switch(index) {
             case(0):
                 break;
             case(3):
                 if(right_side) {
                    child[3] += chopDigits(delta(1.0 - child[3]));
                 }
                 else {
                     child[3] -= chopDigits(delta(child[3] - 0.0));
                 }
                 break;
             case(1):
                 //child[1] = chopDigits(Math.random() * (4 - 2 * child[3]));
                 if(right_side) {
                    child[1] += chopDigits(delta(4.0 - 2.0 * child[3] - child[1]));
                 }
                 else {
                     child[1] -= chopDigits(delta(child[1] - 0.0));
                 }
                 break;
             case(2):
                 if(right_side) {
                    child[2] += chopDigits(delta(Math.min(4.0 - child[1] / 3.0, child[1] / 3.0) - child[2]));
                 }
                 else {
                     child[2] -= chopDigits(delta(child[2] - 0.0));
                 }
                 break;
         }
         
         //If x4, x2, or x3 have changed, we may need to cascade the change to the others whose constraints depend on it
         child = checkConstraints(child);
         return child;
     }
         
     private double delta(double x) {
         return (x * Math.random() * (double)(MAX_GENERATION_COUNT - generation_count)) / ((double)MAX_GENERATION_COUNT);
     }
    
     private double[] mutateBoundary(double[] child) {
         
         int index = (int)(3 * Math.random()) + 1;
         
         boolean right_side = true;
         if(Math.random() < 0.5)
             right_side = false;
         
         switch(index) {
             case(0):
                 break;
             case(3):
                 if(right_side) {
                     child[3] = 1.0;
                 }
                 else {
                     child[3] = 0.0;
                 }
                 break;
             case(1):
                 if(right_side) {
                     child[1] = 4.0 - 2 * child[3];
                 }
                 else {
                     child[1] = 0.0;
                 }
                 break;
             case(2):
                 if(right_side) {
                     child[2] = chopDigits(Math.min(4.0 - child[1] / 3.0, child[1] / 3.0));
                 }
                 else {
                     child[2] = 0.0;
                 }
                 break;
         }

         child = checkConstraints(child);
         return child;
     }
     
     
    /** Sends the String message to the ChessBoard to be added to the end of the current line of output 
     * If there is no ChessBoard, it sends it to System.out.print()
     */
    private void output(String message) {
        if(cgui != null) {
            cgui.output(message);/**/
        }
        else {
            System.out.print(message);
        }
    }
    
    protected void output(String message, boolean verbose_override) {
        if(!verbose_override && !verbose) {
            return;
        }
        else {
            output(message);
        }
    }
    
    /** Sends the String message to the ChessBoard to bea new line of utput 
     * If there is no ChessBoard, it sends it to System.out.println()
     */
    protected void outputLine(String message) {
        if(cgui != null) {
            cgui.output(message + "\n");/**/
        }
        else {
            System.out.println(message);
        }
    }
    
    protected void outputLine(String message, boolean verbose_override) {
        if(verbose_override || verbose) {
            outputLine(message);
        }
    }
    
    private double randomX4(double[] chromosome) {
        return chopDigits(Math.random());
    }
    
    private double randomX2(double[] chromosome) {
        return chopDigits(Math.random() * (4 - 2 * chromosome[3]));
    }
    
    private double randomX3(double[] chromosome) {
        return chopDigits(Math.random() * Math.min(4 - chromosome[1] / 3.0, chromosome[1] / 3.0));
    }
    
    protected void runGeneticSimulation() {
        createGeneticPopulation();
        individual_count = 0;
        generation_count = 0;
        sortPopulation();      
        outputLine("\n\n", false);
        displayPopulation();
        
        for(int i = 0; i < MAX_GENERATION_COUNT; i++) {
            try {
            doOneGeneration();
            }
            catch(SolutionFoundException sfe) { 
                outputLine(sfe.getMessage(), false); 
                i = MAX_GENERATION_COUNT; 
                if(sfe.getMessage().equals("Minimum found"))
                    percent_genetic_solution_found++;
            }
        }
        
        outputLine("\n", false);
        displayPopulation();
        outputLine("number of generations:  " + Integer.toString(generation_count), false);
        outputLine("number of individuals:  " + Integer.toString(individual_count), false);
        outputLine("best individual:", false);
        displayChromosome(population[0], false);
        
        ave_genetic_generations += generation_count;
        ave_genetic_individuals += individual_count;
        
        ave_genetic_min_x1_value += population[0][0];
        ave_genetic_min_x2_value += population[0][1];
        ave_genetic_min_x3_value += population[0][2];
        ave_genetic_min_x4_value += population[0][3];
        
        ave_genetic_fitness += population[0][4];
    }
    
    protected void runSimulation() {
        verbose = false;
        ave_genetic_generations = 0;
        ave_ES_generations = 0;
        ave_genetic_individuals = 0;
        ave_ES_individuals = 0;
        
        percent_genetic_solution_found = 0.0;
        percent_ES_solution_found = 0.0;
        ave_genetic_min_x1_value = 0.0;
        ave_genetic_min_x2_value = 0.0;
        ave_genetic_min_x3_value = 0.0;
        ave_genetic_min_x4_value = 0.0;
        ave_ES_min_x1_value = 0.0;
        ave_ES_min_x2_value = 0.0;
        ave_genetic_fitness = 0.0;
        ave_ES_fitness = 0.0;
        
        outputLine("\n\n");
        
        outputLine("crossover probability = " + Double.toString(crossover_probability));
        outputLine("boundary mutation probability = " + Double.toString(boundary_mutation_probability));
        outputLine("uniform mutation probability = " + Double.toString(uniform_mutation_probability));
        outputLine("nonuniform mutation probability = " + Double.toString(nonuniform_mutation_probability));
        outputLine("population size = " + Integer.toString(population_size));
        
        for(int i = 0; i < 20; i++) {
            runGeneticSimulation();
            //runESSimulation();
        }
        
        ave_genetic_generations /= 20;
        ave_genetic_individuals /= 20;
        
        percent_genetic_solution_found /= 20.0;
        
        ave_genetic_fitness /= 20.0;
        
        ave_genetic_min_x1_value /= 20.0;
        ave_genetic_min_x2_value = chopDigits(ave_genetic_min_x2_value, 3) / 20.0;
        ave_ES_min_x1_value /= 20.0;
        ave_ES_min_x2_value /= 20.0;
        
        outputLine("", true);
        outputLine("average number of generations = " + Integer.toString(ave_genetic_generations), true);
        outputLine("average number of individuals = " + Integer.toString(ave_genetic_individuals), true);
        outputLine("average fitness of best individual = " + Double.toString(ave_genetic_fitness), true);
        outputLine("percent of times solution found = " + Double.toString(percent_genetic_solution_found * 100.00) + "%", true);
        
        outputLine("average x1 value = " + Double.toString(ave_genetic_min_x1_value), true);
        outputLine("average x2 value = " + Double.toString(ave_genetic_min_x2_value), true);
        outputLine("average x3 value = " + Double.toString(ave_genetic_min_x3_value), true);
        outputLine("average x4 value = " + Double.toString(ave_genetic_min_x4_value), true);
        System.out.flush();
    }
    
    protected void runCompleteSimulation() {
        
        //runSimulation();
        
        /**crossover_probability = 0.2;
        boundary_mutation_probability = 0.2;
        uniform_mutation_probability = 0.2;
        nonuniform_mutation_probability = 0.2;
        
        /**for(; crossover_probability < 1.0; crossover_probability += 0.3) {
            for(; boundary_mutation_probability < 1.0; boundary_mutation_probability += 0.3) {
                for(; uniform_mutation_probability < 1.0; uniform_mutation_probability += 0.3) {
                    for(; nonuniform_mutation_probability <= 1.0; nonuniform_mutation_probability += 0.3) {
                        outputLine("\n\n");
                        runSimulation();
                    }
                    nonuniform_mutation_probability = 0.2;
                }
                uniform_mutation_probability = 0.2;
            }
            boundary_mutation_probability = 0.2;
        }/**/
        
        //runSimulation();
        
        /**/crossover_probability = 0.8;
        boundary_mutation_probability = 0.8;
        uniform_mutation_probability = 0.2;
        nonuniform_mutation_probability = 0.8;
        runSimulation();
        
        crossover_probability = 0.2;
        boundary_mutation_probability = 0.8;
        uniform_mutation_probability = 0.2;
        nonuniform_mutation_probability = 0.2;
        runSimulation();
        
        crossover_probability = 0.2;
        boundary_mutation_probability = 0.2;
        uniform_mutation_probability = 0.8;
        nonuniform_mutation_probability = 0.2;
        runSimulation();
        
        crossover_probability = 0.2;
        boundary_mutation_probability = 0.8;
        uniform_mutation_probability = 0.2;
        nonuniform_mutation_probability = 0.5;
        runSimulation();
        
        crossover_probability = 0.2;
        boundary_mutation_probability = 0.5;
        uniform_mutation_probability = 0.8;
        nonuniform_mutation_probability = 0.2;
        runSimulation();
        
        crossover_probability = 0.5;
        boundary_mutation_probability = 0.2;
        uniform_mutation_probability = 0.5;
        nonuniform_mutation_probability = 0.8;
        runSimulation();
        
        crossover_probability = 0.5;
        boundary_mutation_probability = 0.2;
        uniform_mutation_probability = 0.8;
        nonuniform_mutation_probability = 0.5;
        runSimulation();
        
        crossover_probability = 0.5;
        boundary_mutation_probability = 0.2;
        uniform_mutation_probability = 0.8;
        nonuniform_mutation_probability = 0.8;
        runSimulation();
        
        crossover_probability = 0.8;
        boundary_mutation_probability = 0.2;
        uniform_mutation_probability = 0.5;
        nonuniform_mutation_probability = 0.8;
        runSimulation();
        
        crossover_probability = 0.8;
        boundary_mutation_probability = 0.2;
        uniform_mutation_probability = 0.8;
        nonuniform_mutation_probability = 0.2;
        runSimulation();
        
        crossover_probability = 0.8;
        boundary_mutation_probability = 0.5;
        uniform_mutation_probability = 0.8;
        nonuniform_mutation_probability = 0.2;
        runSimulation();
        /**/
        
        /**population_size = 20;
        population = new double[population_size][5];
        
        crossover_probability = 0.8;
        boundary_mutation_probability = 0.8;
        uniform_mutation_probability = 0.2;
        nonuniform_mutation_probability = 0.8;
        runSimulation();
        
        crossover_probability = 0.2;
        boundary_mutation_probability = 0.8;
        uniform_mutation_probability = 0.2;
        nonuniform_mutation_probability = 0.2;
        runSimulation();
        
        crossover_probability = 0.8;
        boundary_mutation_probability = 0.2;
        uniform_mutation_probability = 0.5;
        nonuniform_mutation_probability = 0.8;
        runSimulation();
        
        crossover_probability = 0.8;
        boundary_mutation_probability = 0.2;
        uniform_mutation_probability = 0.8;
        nonuniform_mutation_probability = 0.2;
        runSimulation();
        
        
        population_size = 200;
        population = new double[population_size][5];
        
        crossover_probability = 0.8;
        boundary_mutation_probability = 0.8;
        uniform_mutation_probability = 0.2;
        nonuniform_mutation_probability = 0.8;
        runSimulation();
        
        crossover_probability = 0.2;
        boundary_mutation_probability = 0.8;
        uniform_mutation_probability = 0.2;
        nonuniform_mutation_probability = 0.2;
        runSimulation();
        
        crossover_probability = 0.8;
        boundary_mutation_probability = 0.2;
        uniform_mutation_probability = 0.5;
        nonuniform_mutation_probability = 0.8;
        runSimulation();
        
        crossover_probability = 0.8;
        boundary_mutation_probability = 0.2;
        uniform_mutation_probability = 0.8;
        nonuniform_mutation_probability = 0.2;
        runSimulation();
        /**/
        
    }
    
    protected void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
     /** Does an Insertion Sort on the population by fitness */
    private void sortPopulation() {
        double[] current = new double[5];
        
        //population_size = population.size();
        for(int i = 1; i < population_size; i++) {
            
            //current is the one we're going to put in the right place
            //Individual current = (Individual)(population.get(i));
            //double[] current = population[i];
            for(int k = 0; k < population[i].length; k++) {
                current[k] = population[i][k];
            }
            //output("current =    ");
            //displayChromosome(current);
            double current_fitness = current[4];
            
            //Test to see if current's fitness is better than the previous one; if so, then it's out of order
            double last_fitness = population[i - 1][4];
            
            if(current_fitness < last_fitness) {
                //outputLine(Double.toString(current_fitness) + " < " + Double.toString(last_fitness));
                //Now, search from the beginning to see where it goes
                int j = 0;
                while(current_fitness >= population[j][4] &&  j < population_size - 1) {
                    //outputLine(Double.toString(current_fitness) + " >= " + Double.toString(population[j][2]));
                    j++;
                }
                //We've found the correct place
                //outputLine("j = " + Integer.toString(j));
                for(int k = i; k > j; k--) {
                    population[k][0] = population[k - 1][0];
                    population[k][1] = population[k - 1][1];
                    population[k][2] = population[k - 1][2];
                    population[k][3] = population[k - 1][3];
                    population[k][4] = population[k - 1][4];
                }
                population[j][0] = current[0];
                population[j][1] = current[1];
                population[j][2] = current[2];
                population[j][3] = current[3];
                population[j][4] = current[4];
                
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Constrained c = new Constrained();
        c.runGeneticSimulation();   
    }
}
