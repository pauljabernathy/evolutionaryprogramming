/*
 * ChessBoard.java
 *
 * Created on February 6, 2006, 8:38 AM
 */

package evolution.queens;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 *
 * @author  Paul J. Abernathy
 */
public class ChessBoard extends JFrame {
    
    private JPanel panel;
    private JPanel button_panel;
    private JTextArea outputTextArea;
    private JButton start_button;
    private Box vertical_box_layout;
    
    /** Creates a new instance of ChessBoard */
    public ChessBoard() {
        setTitle("The Eight Queens Program");
        setSize(200, 200);
        
        panel = new JPanel();
        button_panel = new JPanel();
        outputTextArea = new JTextArea(10, 30);
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
        final ChessBoard board = this;
        start_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Queens queens = new Queens(100, 8, 10000, 0.9, 0.5, board);
                queens.runSimulation();
            }
        });
        
        
        button_panel.add(start_button);
    }
    
    protected void output(String message) {
        outputTextArea.append(message);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       ChessBoard chessboard = new ChessBoard();
       WindowListener wl = new WindowAdapter() {  public void windowClosing(WindowEvent we) { System.exit(0); } };
       chessboard.addWindowListener(wl);
        
    }
}
