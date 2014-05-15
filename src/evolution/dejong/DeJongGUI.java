/*
 * DeJongGUI.java
 *
 * Created on February 20, 2006, 10:07 AM
 */

package evolution.dejong;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 *
 * @author  Paul J. Abernathy
 */
public class DeJongGUI extends JFrame {
    
    private JPanel panel;
    private JPanel button_panel;
    private JTextArea outputTextArea;
    
    private JButton genetic_button;
    private JButton es_button;
    private JButton complete_button;
    private JButton start_button;
    
    private Box vertical_box_layout;
    
    /** Creates a new instance of DeJongGUI */
    public DeJongGUI() {
        setTitle("The DeJong Program");
        setSize(200, 200);
        
        panel = new JPanel();
        button_panel = new JPanel();
        outputTextArea = new JTextArea(50, 30);
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
        final DeJongGUI board = this;
        start_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                DeJong dj = new DeJong(board);
                dj.runSimulation();
            }
        });
        
        genetic_button = new JButton("One Genetic Run");
        genetic_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                DeJong dj = new DeJong(board);
                dj.setVerbose(true);
                dj.runGeneticSimulation();
            }
        });
        
        es_button = new JButton("One ES Run");
        es_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                DeJong dj = new DeJong(board);
                dj.setVerbose(true);
                dj.runESSimulation();
            }
        });
        
        complete_button = new JButton("Complete Simulation");
        complete_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                DeJong dj = new DeJong(board);
                dj.setVerbose(false);
                dj.runCompleteSimulation();
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
       DeJongGUI djgui = new DeJongGUI();
       WindowListener wl = new WindowAdapter() {  public void windowClosing(WindowEvent we) { System.exit(0); } };
       djgui.addWindowListener(wl);
        
    }
    
}
