/*
 * DeJong.java
 *
 * Created on February 18, 2006, 7:45 PM
 */

package evolution.dejong;
import evolution.*;
//import evolution.queens.Individual;
import java.util.Vector;

/**
 *
 * @author  Paul J. Abernathy
 */
public class DeJong {
    
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
    
    private DeJongGUI djgui;
    boolean verbose;
    
    private int ave_genetic_generations;
    private int ave_ES_generations;
    private int ave_genetic_individuals;
    private int ave_ES_individuals;
    private double percent_genetic_solution_found;
    private double percent_ES_solution_found;
    private double ave_genetic_min_x1_value;
    private double ave_genetic_min_x2_value;
    private double ave_ES_min_x1_value;
    private double ave_ES_min_x2_value;
    private double ave_genetic_fitness;
    private double ave_ES_fitness;
    
    private boolean individual_termination; //true if terminates after given # of individuals
    //false if terminates after given # of generationss
    
    /** Creates a new instance of DeJong */
    public DeJong() {
        
        alpha = 0.5;
        sigma = 0.2;
        mutation_probability = 0.8;
        crossover_probability = 0.8;
        
        individual_count = 0;
        MAX_INDIVIDUAL_COUNT = 10000;
        
        num_generations = 500;
        MAX_GENERATION_COUNT = num_generations;
        population_size = 50;
        ES_population_size = 1;
        population = new double[population_size][3];
        ES_population = new double[ES_population_size][3];
        
        individual_termination = true;
        verbose = true;
        
        ave_genetic_generations = 0;
        ave_ES_generations = 0;
        ave_genetic_individuals = 0;
        ave_ES_individuals = 0;
        
        percent_genetic_solution_found = 0.0;
        percent_ES_solution_found = 0.0;
        ave_genetic_min_x1_value = 0.0;
        ave_genetic_min_x2_value = 0.0;
        ave_ES_min_x1_value = 0.0;
        ave_ES_min_x2_value = 0.0;
        ave_genetic_fitness = 0.0;
        ave_ES_fitness = 0.0;
    }
    
    public DeJong(DeJongGUI djgui) {
        this();
        this.djgui = djgui;
    }
    
    protected double chopDigits(double value) {
        return (double)((int)(value * 1000)) / 1000.0;
    }
    
    private double[] crossover(double[] mom, double[] dad) {
        int size = mom.length;
        double[] child = new double[size];    //length should be 2
        for(int i = 0; i < size - 1; i++) {
            child[i] = chopDigits(alpha * mom[i] + (1.0 - alpha) * dad[i]);
        }
        child[size - 1] = chopDigits(evaluateFitness(child[0], child[1]));
        return child;
    }
    
    private void createESPopulation() {
        int value = 0;
        double[] chromosome = new double[2];
        for(int i = 0; i < ES_population_size; i++) {
            double x1 = randomGene();
            double x2 = randomGene();
            ES_population[i] = new double[] {x1, x2, chopDigits(evaluateFitness(x1, x2)) };
            individual_count++;
        }
    }
    
    private void createGeneticPopulation() {      
        int value = 0;
        double[] chromosome = new double[2];
        for(int i = 0; i < population_size; i++) {
            double x1 =  randomGene();
            double x2 =  randomGene();
            population[i] = new double[] {x1, x2, chopDigits(evaluateFitness(x1, x2)) };
            individual_count++;
        }
    }
    
    private void displayChromosome(double[] chromosome, boolean verbose_override) {
        outputLine("x1 = " + Double.toString(chromosome[0]) + 
                "    x2 = " + Double.toString(chromosome[1]) + "    fitness = " + Double.toString(chromosome[2]), verbose_override);
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
    
    private void displayESPopulation() {
        outputLine("", false);
        outputLine("Displaying Population.", false);
        double gene = 1.0;
        double gene2 = 1.0;
        for(int i = 0; i < ES_population_size; i++) {
            displayChromosome(ES_population[i], false);
        } 
    }
        
    private void doOneGeneration() throws SolutionFoundException {
        generation_count++;
        for(int r = 0; r < population_size - 1; r++) {
            double[] child = mate(population[r], population[r + 1]);
            //displayChromosome(child);
            insertIntoPopulation(child);
        }
        if(generation_count >= MAX_GENERATION_COUNT && !individual_termination) {
            throw new SolutionFoundException("Maximum number of generations done.");
        }
    }
    
    private void doOneESGeneration() throws SolutionFoundException {
        generation_count++;
        double[] child = new double[3];
        
        for(int i = 0; i < ES_population_size; i++) {
            child[0] = chopDigits(ES_population[i][0] + gaussRandom());
            child[1] = chopDigits(ES_population[i][1] + gaussRandom());
            child[2] = chopDigits(evaluateFitness(child[0], child[1]));
            individual_count++;
            displayChromosome(child, false);
            //insertIntoESPopulation(child);
            
            if(child[2] < ES_population[i][2]) {
                ES_population[i][0] = child[0];
                ES_population[i][1] = child[1];
                ES_population[i][2] = child[2];
            }
            if(child[2] == 0.0) {
                throw new SolutionFoundException("Minimum found");
            }
            /**/if(individual_count >= MAX_INDIVIDUAL_COUNT && individual_termination) {
                throw new SolutionFoundException("Maximum number of individuals created.");
            }/**/
            if(generation_count >= MAX_GENERATION_COUNT && !individual_termination) {
                throw new SolutionFoundException("Maximum number of generations done.");
            }
        }
    }
    
    private double evaluateFitness(double[] chromosome) {
        double x1 = chromosome[0];
        double x2 = chromosome[1];
        return 100.0 * (x1 * x1 - x2) * (x1 * x1 - x2) + (1 - x1) * (1 - x1);
    }
    
    private double evaluateFitness(double x1, double x2) {
        return 100.0 * (x1 * x1 - x2) * (x1 * x1 - x2) + (1 - x1) * (1 - x1);
    }
    
    private double getPhenotype(int gene) {
        double long_value = (double)(0.016062745098 * gene) - 2.048;
        return (double)((int)(long_value * 1000)) / 1000.0;
    }
    
    private double gaussRandom() {
        int num_rands = 12;
        double mu = 0.0;
        double q = mu;
        double sum = 0.0;
        //double[] rand_nums = new double[num_rands];
        for(int s = 0; s < num_rands; s++) {
            //rand_nums[s] = Math.random();
            sum += Math.random();//rand_nums[s];
        }
        
        q += sigma * (sum - num_rands / 2);
        return q;
    }
    
    private void insertIntoPopulation(double[] child) {
        for(int i = 0; i < population_size; i++) {
            if(population[i][2] > child[2]) {
                population[i] = child;
                i = population_size;
            }
        }
    }
    
    private void insertIntoESPopulation(double[] child) {
        for(int i = 0; i < ES_population_size; i++) {
            if(ES_population[i][2] > child[2]) {
                ES_population[i] = child;
                i = ES_population_size;
            }
        }
    }
        
    private double[] mate(double[] mom, double[] dad) throws SolutionFoundException {
        double[] child = crossover(mom, dad);
        
        /**if(Math.random() < crossover_probability) {
            crossover(mom, dad);
        }/**/
        
        individual_count++;
        
        if(Math.random() < mutation_probability) {
            child[0] += gaussRandom();
            child[0] = chopDigits(child[0]);
        }
        if(Math.random() < mutation_probability) {
            child[1] += gaussRandom();
            child[1] = chopDigits(child[1]);
            child[2] = chopDigits(evaluateFitness(child));
        }
        
        if(child[2] == 0.0) {
            insertIntoPopulation(child);
                throw new SolutionFoundException("Minimum found");
        }
        /**/if(individual_count >= MAX_INDIVIDUAL_COUNT && individual_termination) {
            insertIntoPopulation(child);
            throw new SolutionFoundException("Maximum number of individuals created.");
        }/**/
        return child;
    }
    
    private void mateES(double[] ma, double[] pa) {
        
        
    }
    /** Sends the String message to the ChessBoard to be added to the end of the current line of output 
     * If there is no ChessBoard, it sends it to System.out.print()
     */
    private void output(String message) {
        if(djgui != null) {
            djgui.output(message);/**/
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
        if(djgui != null) {
            djgui.output(message + "\n");/**/
        }
        else {
            System.out.println(message);
        }
    }
    
    protected void outputLine(String message, boolean verbose_override) {
        if(!verbose_override && !verbose) {
            return;
        }
        else {
            outputLine(message);
        }
    }
    
    
    private double randomGene() {
        return chopDigits((Math.random() * 4.096) - 2.048);
    }
    
    /** Does an Insertion Sort on the population by fitness */
    private void sortPopulation() {
        double[] current = new double[3];
        
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
            double current_fitness = current[2];
            
            //Test to see if current's fitness is better than the previous one; if so, then it's out of order
            double last_fitness = population[i - 1][2];
            
            if(current_fitness < last_fitness) {
                //outputLine(Double.toString(current_fitness) + " < " + Double.toString(last_fitness));
                //Now, search from the beginning to see where it goes
                int j = 0;
                while(current_fitness >= population[j][2] &&  j < population_size - 1) {
                    //outputLine(Double.toString(current_fitness) + " >= " + Double.toString(population[j][2]));
                    j++;
                }
                //We've found the correct place
                //outputLine("j = " + Integer.toString(j));
                for(int k = i; k > j; k--) {
                    population[k][0] = population[k - 1][0];
                    population[k][1] = population[k - 1][1];
                    population[k][2] = population[k - 1][2];
                }
                population[j][0] = current[0];
                population[j][1] = current[1];
                population[j][2] = current[2];
                
            }
        }
    }
    
    protected void runCompleteSimulation() {
        outputLine("sigma = " + Double.toString(sigma));
        outputLine("mutation probability = " + Double.toString(mutation_probability));
        runSimulation();
        sigma = 0.5;
        outputLine("\n");
        outputLine("sigma = " + Double.toString(sigma));
        outputLine("mutation probability = " + Double.toString(mutation_probability));
        runSimulation();
        
        sigma = 0.2;
        mutation_probability = 0.5;
        outputLine("\n");
        outputLine("sigma = " + Double.toString(sigma));
        outputLine("mutation probability = " + Double.toString(mutation_probability));
        runSimulation();
        
        sigma = 0.5;
        outputLine("\n");
        outputLine("sigma = " + Double.toString(sigma));
        outputLine("mutation probability = " + Double.toString(mutation_probability));
        runSimulation();
        
        mutation_probability = 0.0;
        sigma = 1.0;
        outputLine("\n");
        outputLine("sigma = " + Double.toString(sigma));
        outputLine("mutation probability = " + Double.toString(mutation_probability));
        runSimulation();
        
        mutation_probability = 1.0;
        sigma = 0.0;
        outputLine("\n");
        outputLine("sigma = " + Double.toString(sigma));
        outputLine("mutation probability = " + Double.toString(mutation_probability));
        runSimulation();
        
        mutation_probability = 0.5;
        sigma = 2.0;
        outputLine("\n");
        outputLine("sigma = " + Double.toString(sigma));
        outputLine("mutation probability = " + Double.toString(mutation_probability));
        runSimulation();
        
        mutation_probability = 1.0;
        sigma = 2.0;
        outputLine("\n");
        outputLine("sigma = " + Double.toString(sigma));
        outputLine("mutation probability = " + Double.toString(mutation_probability));
        runSimulation();
        
        mutation_probability = 0.0;
        sigma = 2.0;
        outputLine("\n");
        outputLine("sigma = " + Double.toString(sigma));
        outputLine("mutation probability = " + Double.toString(mutation_probability));
        runSimulation();
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
        ave_ES_min_x1_value = 0.0;
        ave_ES_min_x2_value = 0.0;
        ave_genetic_fitness = 0.0;
        ave_ES_fitness = 0.0;
        
        for(int i = 0; i < 20; i++) {
            runGeneticSimulation();
            runESSimulation();
        }
        
        ave_genetic_generations /= 20;
        ave_ES_generations /= 20;
        ave_genetic_individuals /= 20;
        ave_ES_individuals /= 20;
        
        percent_genetic_solution_found /= 20.0;
        percent_ES_solution_found /= 20.0;
        
        ave_genetic_fitness /= 20.0;
        ave_ES_fitness /= 20.0;
        
        ave_genetic_min_x1_value /= 20.0;
        ave_genetic_min_x2_value /= 20.0;
        ave_ES_min_x1_value /= 20.0;
        ave_ES_min_x2_value /= 20.0;
        
        outputLine("", true);
        outputLine("ave_genetic_generations = " + Integer.toString(ave_genetic_generations), true);
        outputLine("ave_ES_generations = " + Integer.toString(ave_ES_generations), true);
        outputLine("ave_genetic_individuals = " + Integer.toString(ave_genetic_individuals), true);
        outputLine("ave_ES_individuals = " + Integer.toString(ave_ES_individuals), true);
        outputLine("ave_genetic_fitness = " + Double.toString(ave_genetic_fitness), true);
        outputLine("ave_ES_fitness = " + Double.toString(ave_ES_fitness), true);
        outputLine("percent_genetic_solution_found = " + Double.toString(percent_genetic_solution_found), true);
        outputLine("percent_ES_solution_found = " + Double.toString(percent_ES_solution_found), true);
        
        outputLine("ave_genetic_min_x1_value = " + Double.toString(ave_genetic_min_x1_value), true);
        outputLine("ave_genetic_min_x2_value = " + Double.toString(ave_genetic_min_x2_value), true);
        outputLine("ave_ES_min_x1_value = " + Double.toString(ave_ES_min_x1_value), true);
        outputLine("ave_ES_min_x1_value = " + Double.toString(ave_ES_min_x2_value), true);
        
    }
    
    protected void runGeneticSimulation() {
        createGeneticPopulation();
        individual_count = 0;
        generation_count = 0;
        //displayPopulation();
        sortPopulation();
        displayPopulation();
        
        outputLine("Genetic Simulation", false);
        
        try {
            //for(int q = 0; q < num_generations; q++) {
            while(individual_count < MAX_INDIVIDUAL_COUNT) { // || generation_count < MAX_GENERATION_COUNT) {
                doOneGeneration();
            }
        }
        catch(SolutionFoundException sfe) { 
            displayPopulation();
            outputLine("\n" + sfe.getMessage(), false); 
            outputLine("number of generations:  " + Integer.toString(generation_count), false);
            outputLine("number of individuals created:  " + Integer.toString(individual_count), false);
            outputLine("Best Individual:", false);
            displayChromosome(population[0], false);    //population[0] _should_ always be the best
        
        }
        //displayPopulation();
        ave_genetic_generations += generation_count;
        ave_genetic_individuals += individual_count; //will be averaged in rumSimulation(); we're just adding them up now
        if(population[0][2] == 0.0) {
            percent_genetic_solution_found++;
        }
        ave_genetic_min_x1_value += population[0][0];
        ave_genetic_min_x2_value += population[0][1];
        ave_genetic_fitness += population[0][2];
    }
    
    protected void runESSimulation() {
        individual_count = 0;
        generation_count = 0;
        //displayESPopulation();
        //sortPopulation();
        createESPopulation();
        displayESPopulation();
        
        outputLine("ES Simulation", false);
        try{
            //for(int q = 0; q < num_generations; q++) {
            while(individual_count < MAX_INDIVIDUAL_COUNT || generation_count < MAX_GENERATION_COUNT) {
                doOneESGeneration();
            }
        }
        /**catch(SolutionFoundException sfe) { outputLine(sfe.getMessage()); }
        displayESPopulation();/**/
        catch(SolutionFoundException sfe) { outputLine("\n" + sfe.getMessage(), false); }
        outputLine("number of generations:  " + Integer.toString(generation_count), false);
        outputLine("number of individuals created:  " + Integer.toString(individual_count), false);
        outputLine("Best Individual:", false);
        displayChromosome(ES_population[0], false);    //population[0] _should_ always be the best
        
        ave_ES_generations += generation_count;
        ave_ES_individuals += individual_count; //will be averaged in rumSimulation(); ; we're just adding them up now
        if(ES_population[0][2] == 0.0) {
            percent_ES_solution_found++;
        }
        ave_ES_min_x1_value += ES_population[0][0];
        ave_ES_min_x2_value += ES_population[0][1];
        ave_ES_fitness += ES_population[0][2];
        
        
    }
    
    protected void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DeJong dj = new DeJong();
        dj.runSimulation();
    }
}
