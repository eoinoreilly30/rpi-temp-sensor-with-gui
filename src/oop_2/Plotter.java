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
	
	protected int data[];
	
    public Plotter() {
    	this.data = new int[20];
        setBorder(BorderFactory.createLineBorder(Color.black));      
    }
    
    public void clearDataPoints() {
    	for (int i = 0; i < data.length; i++) {
    		data[i] = 0;
    	}
    }
    
    public void addDataPoint(int newDataPoint) {
    	// shift points to right
    	for (int i = (data.length-2); i >= 0; i--) {
    		data[i+1] = data[i];
    	}
    	data[0] = newDataPoint;
    	this.repaint();
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
        int y_offset = (int) (height*(90.0f/100));
        
        // draw axes
        g.setColor(Color.gray);
        g.drawLine(x_offset, 0, x_offset, height);
        g.drawLine(0, y_offset, width, y_offset);
        
        // draw y axis labels
        int yStepSize = (int) (y_offset/10.0f);
        g.setColor(Color.black);
        for (int i = 0; i < 10; i++) {
        	g.drawString(Integer.toString(i*10), x_offset-20, y_offset-(yStepSize*i));
        }
        
        // draw x axis labels
        int xStepSize = (int) ((width-x_offset)/20.0f);
        for (int i = 0; i < 20; i++) {
        	g.drawString(Integer.toString(i), x_offset+(xStepSize*i), y_offset+12);
        }
        
        // draw max and min lines
//        int max = Arrays.stream(data).max().getAsInt();
//    	int min = Arrays.stream(data).min().getAsInt();
//    	// scale data points
//    	max = (int) ((max/100.0f)*y_offset);
//    	min = (int) ((min/100.0f)*y_offset);
//    	g.setColor(Color.blue);
//    	g.drawString("MAX", 3, y_offset-max);
//    	g.drawString("MIN", 3, y_offset-min);
//    	g.drawLine(0, y_offset-max, width, y_offset-max);
//    	g.drawLine(0, y_offset-min, width, y_offset-min);
        
        // draw data points
        g.setColor(Color.green);
        for (int i = 0; i < data.length-1; i++) {
        	int point1 = (int) ((data[i]/100.0f)*y_offset);
        	int point2 = (int) ((data[i+1]/100.0f)*y_offset);        	
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
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    	
    }
}