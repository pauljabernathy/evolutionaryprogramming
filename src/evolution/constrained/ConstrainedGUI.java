/*
 * ConstrainedGUI.java
 *
 * Created on March 2, 2006, 8:48 PM
 */

package evolution.constrained;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 *
 * @author  Paul J. Abernathy
 */
public class ConstrainedGUI extends JFrame {
    
    private JPanel panel;
    private JPanel button_panel;
    private JTextArea outputTextArea;
    
    private JButton genetic_button;
    private JButton es_button;
    private JButton complete_button;
    private JButton start_button;
    
    private Box vertical_box_layout;
    
    /** Creates a new instance of ConstrainedGUI */
    public ConstrainedGUI() {
        
        setTitle("");
        setSize(200, 200);
        
        panel = new JPanel();
        button_panel = new JPanel();
        outputTextArea = new JTextArea(50, 50);
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        JScrollPane outputScrollPane = new JScrollPane(outputTextArea);
        
        vertical_box_layout = Box.createVerticalBox();
        setContentPane(panel);
        addButtons();
        
        vertical_box_layout.add(outputScrollPane);
        vertical_box_layout.add(Box.createVerticalStrut(5));
        vertical_box_layout.add(button_panel);
        panel.add(vertical_box_layout, "south");
        
        this.pack();
        this.setVisible(true);
    }
    
    private void addButtons() {
        start_button = new JButton("start");
        final ConstrainedGUI cgui = this;
        start_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                //Constrained = new Constrained(board);
                //j.runSimulation();
            }
        });
        
        genetic_button = new JButton("One Genetic Run");
        genetic_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Constrained c = new Constrained(cgui);
                c.setVerbose(true);
                //c.runGeneticSimulation();
                c.runGeneticSimulation();
            }
        });
        
        es_button = new JButton("One ES Run");
        es_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                output("Not implemented yet.\n");
            }
        });
        
        complete_button = new JButton("Complete Simulation");
        complete_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Constrained c = new Constrained(cgui);
                c.setVerbose(false);
                c.runCompleteSimulation();
            }
        });
        
        //button_panel.add(start_button);
        button_panel.add(genetic_button);
        button_panel.add(es_button);
        button_panel.add(complete_button);
    }
    
    protected void output(String message) {
        outputTextArea.append(message);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       //ChessBoard chessboard = new ChessBoard();
       ConstrainedGUI cgui = new ConstrainedGUI();
       WindowListener wl = new WindowAdapter() {  public void windowClosing(WindowEvent we) { System.exit(0); } };
       cgui.addWindowListener(wl);
        
    }
    
    
}
