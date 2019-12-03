package oop_2;

import java.net.*;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;

@SuppressWarnings("serial")
public class ClientGUI extends JFrame implements ActionListener, FocusListener {
	
    private Plotter dataPlotter;
    private JButton start, stop;
    private JTextField serverIPInput;
	private JTextField sampleRateInput;
    
    public ClientGUI() {
    	createGUI();
    }
	    
    private void createGUI() {
    	JFrame frame = new JFrame("RPi Client");
    	this.dataPlotter = new Plotter();
    	this.serverIPInput = new JTextField("Enter server IP", 10);
    	this.sampleRateInput = new JTextField("Enter sample rate", 10);
    	this.start = new JButton("Start");
    	this.stop = new JButton("Stop");
    	
    	this.serverIPInput.addFocusListener(this);
    	this.sampleRateInput.addFocusListener(this);
    	this.start.addActionListener(this);
        this.stop.addActionListener(this);
    	
    	frame.getContentPane().setLayout(new FlowLayout());
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	
    	frame.add(dataPlotter);
    	frame.add(serverIPInput);
    	frame.add(sampleRateInput);
    	frame.add(start);
    	frame.add(stop);
    	
    	frame.pack();
    	frame.setVisible(true);
    }
    
    @Override
	public void run() {
    	connectToServer(this.serverIPInput.getText());
    	
	}
    
    @Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		if (e.getActionCommand().equals("Start")) {
			
		}
		else if (e.getActionCommand().equals("Stop")) {
			
			try {
	            System.out.println("04. -- Closing down the client socket gracefully.");
	            this.socket.close();
	        }
	        catch (IOException exc) {
	            System.err.println("XX. Could not close client socket. " + exc.getMessage());
	        }
		}
	}
    
    @Override
	public void focusGained(FocusEvent e) {
//    	this.sampleRateInput.setText("");
//    	this.serverIPInput.setText("");
	}

	@Override
	public void focusLost(FocusEvent e) {}

    public static void main(String args[]) 
    {	
    	Client client = new Client();
    	
        // test plotter on random data
//        Random r = new Random();
//        while (true) {
//        	dataPlotter.addDataPoint(r.nextInt((90-30) + 1) + 30);
//        	dataPlotter.repaint();
//        	try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//        }
    	
    	
//    	System.out.println("**. End of Application.");
    }
}