package oop_2;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;

@SuppressWarnings("serial")
public class Plotter extends JPanel {
	
	private double data[];
	
    public Plotter() {
    	this.data = new double[20];
        setBorder(BorderFactory.createLineBorder(Color.black));      
    }
    
    public void addDataPoint(double newDataPoint) {
    	// shift points to right
    	for (int i = (data.length-2); i >= 0; i--) {
    		this.data[i+1] = this.data[i];
    	}
    	this.data[0] = newDataPoint;
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(500, 200);
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // set up axes
        int w = getWidth();
        int h = getHeight();
        int x_offset = (int) (w*(10.0f/100));
        int y_offset = (int) (h*(80.0f/100));
        
        g.setColor(Color.gray);
        g.drawLine(x_offset, 0, x_offset, h);
        g.drawLine(0, y_offset, w, y_offset);
        
        int stepSize = (int) (y_offset/10.0f);
        
        g.setColor(Color.black);
        for (int i = 0; i < 10; i++) {
        	g.drawString(Integer.toString(i*10), x_offset-20, y_offset-(stepSize*i));
        }
        
        
        
    }

    public static void main(String[] args) {
    	JFrame mainFrame = new JFrame("Plot test");
    	Plotter dataPlotter = new Plotter();
    	
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.add(dataPlotter);
        mainFrame.pack();
        mainFrame.setVisible(true);
    	
    }
}