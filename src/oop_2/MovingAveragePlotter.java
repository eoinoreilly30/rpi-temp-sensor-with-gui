package oop_2;

import java.util.Random;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class MovingAveragePlotter extends Plotter {
	
	private int previousAverage = 0;
	
	@Override
	public void clearDataPoints() {
		super.clearDataPoints();
		this.previousAverage = 0;
	}
	
	@Override
	public void addDataPoint(int newDataPoint) {
		int nextAverage = (int) (previousAverage+(1/20.0f)*(newDataPoint-data[data.length-1]));		
		previousAverage = nextAverage;
		
		if (nextAverage > super.max) super.max = nextAverage;
    	if (nextAverage < super.min) super.min = nextAverage;
		
    	// shift points to right
    	for (int i = (data.length-2); i >= 0; i--) {
    		data[i+1] = data[i];
    	}
    	
    	data[0] = nextAverage;
    	this.repaint();
    }

	public static void main(String[] args) {
		JFrame mainFrame = new JFrame("Plot test");
		MovingAveragePlotter dataPlotter = new MovingAveragePlotter();
    	
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
