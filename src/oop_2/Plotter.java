package oop_2;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Random;

@SuppressWarnings("serial")
public class Plotter extends JPanel {
	
	private int data[];
	
    public Plotter() {
    	this.data = new int[20];
        setBorder(BorderFactory.createLineBorder(Color.black));      
    }
    
    public void addDataPoint(int newDataPoint) {
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
        int width = getWidth();
        int height = getHeight();
        int x_offset = (int) (width*(10.0f/100));
        int y_offset = (int) (height*(80.0f/100));
        
        // draw axes
        g.setColor(Color.gray);
        g.drawLine(x_offset, 0, x_offset, height);
        g.drawLine(0, y_offset, width, y_offset);
        
        int yStepSize = (int) (y_offset/10.0f);
        
        // draw y axis labels
        g.setColor(Color.black);
        for (int i = 0; i < 10; i++) {
        	g.drawString(Integer.toString(i*10), x_offset-20, y_offset-(yStepSize*i));
        }
        
        int xStepSize = (int) ((width-x_offset)/20.0f);
        
        // draw data points
        g.setColor(Color.green);
        for (int i = 0; i < this.data.length-1; i++) {
        	// scale data points
        	int point1 = (int) ((this.data[i]/100.0f)*y_offset);
        	int point2 = (int) ((this.data[i+1]/100.0f)*y_offset);        	

        	g.drawLine(x_offset+xStepSize*i, y_offset-point1, x_offset+xStepSize*(i+1), y_offset-point2);
        }
        
    }

    public static void main(String[] args) {
    	JFrame mainFrame = new JFrame("Plot test");
    	Plotter dataPlotter = new Plotter();
    	
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.add(dataPlotter);
        mainFrame.pack();
        mainFrame.setVisible(true);
        
        // test plotter on random data
        Random r = new Random();
        while (true) {
        	dataPlotter.addDataPoint(r.nextInt((90-30) + 1) + 30);
        	dataPlotter.repaint();
        	try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    	
    }
}