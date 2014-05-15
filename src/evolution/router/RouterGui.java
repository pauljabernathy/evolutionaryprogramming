/*
 * RouterGui.java
 *
 * Created on April 13, 2006, 8:36 PM
 */

package evolution.router;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Date;

/**
 *
 * @author  Paul J. Abernathy
 */
public class RouterGui extends JFrame {
    
    private JPanel panel;
    private Box vertical_box_layout;
    private JPanel button_panel;
    private JButton evolutionary_simulation_button;
    private JButton heuristic_simulation_button;
    private JButton exit_button;
    
    private JPanel text_field_panel;
    private JTextField num_generations_field;
    private JTextField population_size_field;
    private JTextField swap_probability_field;
    private JTextField inversion_probability_field;
    //private JTextField 
    
    private JTextArea output_text_area;
    private JScrollPane output_scroll_pane;
    
    private EvolutionaryVehicleRouter router;
    private HeuristicVehicleRouter hvr;
    
    /** Creates a new instance of RouterGui */
    public RouterGui() {
        setTitle("The Vehicle Router");
        setSize(300, 300);
        
        panel = new JPanel();
        output_text_area = new JTextArea(15, 70);
        output_scroll_pane = new JScrollPane(output_text_area);
        //panel.add(output_scroll_pane);
        setContentPane(panel);
        
        vertical_box_layout = Box.createVerticalBox();
        panel.add(vertical_box_layout, "South");
        vertical_box_layout.add(output_scroll_pane);
        
        vertical_box_layout.add(Box.createVerticalStrut(5));
        vertical_box_layout.add(addTextFields());
        
        vertical_box_layout.add(Box.createVerticalStrut(5));
        vertical_box_layout.add(makeButtonPanel());
        
        pack();
        setVisible(true);
        router = new EvolutionaryVehicleRouter(this);
        //router = new VehicleRouter(this);
        hvr = new HeuristicVehicleRouter(this);
        
        WindowListener wl = new WindowAdapter() { 
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }};
        addWindowListener(wl);
    }
    
    private void doSimulation(boolean evolutionary) {
        int population_size;
        int max_generations;
    
        double swap_mutation_probability;
        double inversion_mutation_probability;
        try {
            population_size = Integer.parseInt(population_size_field.getText());
        }
        catch(NumberFormatException nfe) { population_size = 5; }
        try {
            max_generations = Integer.parseInt(num_generations_field.getText());
        }
        catch(NumberFormatException nfe) { max_generations = 500; }
        
        try {
            swap_mutation_probability = Double.parseDouble(swap_probability_field.getText());
            if(swap_mutation_probability < 0.0 || swap_mutation_probability > 1.0)
                throw new NumberFormatException("parameter out of range");
        }
        catch(NumberFormatException nfe) { swap_mutation_probability = 0.7; }
        try {
            inversion_mutation_probability = Double.parseDouble(inversion_probability_field.getText());
            if(inversion_mutation_probability < 0.0 || inversion_mutation_probability > 1.0)
                throw new NumberFormatException("parameter out of range");
        }
        catch(NumberFormatException nfe) { inversion_mutation_probability = 0.3; }
        
        //Date start_time = new Date();
        if(evolutionary)
            router.runSimulation(population_size, max_generations, inversion_mutation_probability, swap_mutation_probability);
        else
            hvr.runSimulation();
        //Date stop_time = new Date();
        //output("running time:  " + Long.toString(stop_time.getTime() - start_time.getTime()));
        
    }
    
    private JPanel addTextFields() {
        text_field_panel = new JPanel();
        GridLayout grid_layout = new GridLayout(4, 2);
        text_field_panel.setLayout(grid_layout);
        
        JLabel gen_label = new JLabel("Number of Generations:");
        num_generations_field = new JTextField(20);
        JLabel pop_size_label = new JLabel("Population Size:");
        population_size_field = new JTextField(20);
        JLabel swap_label = new JLabel("Swap Mutation Probability:");
        swap_probability_field = new JTextField(0);
        JLabel inv_label = new JLabel("Inversion Mutation Probability:");
        inversion_probability_field = new JTextField(0);
        
        text_field_panel.add(gen_label);
        text_field_panel.add(num_generations_field);
        text_field_panel.add(pop_size_label);
        text_field_panel.add(population_size_field);
        text_field_panel.add(swap_label);
        text_field_panel.add(swap_probability_field);
        text_field_panel.add(inv_label);
        text_field_panel.add(inversion_probability_field);
        
        //vertical_box_layout.add(text_field_panel);
        
        return text_field_panel;
    }
    
    private JPanel makeButtonPanel() {
        button_panel = new JPanel();
        evolutionary_simulation_button = new JButton("Run Simulation");
        vertical_box_layout.add(button_panel, "South");
        evolutionary_simulation_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                doSimulation(true);
            }});
        
        button_panel.add(evolutionary_simulation_button);
        heuristic_simulation_button = new JButton("Heuristic Search");
        heuristic_simulation_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                doSimulation(false);
            }});
        button_panel.add(heuristic_simulation_button);
        
        exit_button = new JButton("Exit");
        exit_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }});
        button_panel.add(exit_button);
        return button_panel;
    }
    
    protected void output(String message) {
        output_text_area.append(message);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RouterGui rgui = new RouterGui();
    }
    
}
