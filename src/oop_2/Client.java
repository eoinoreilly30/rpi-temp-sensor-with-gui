package oop_2;

import java.net.*;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@SuppressWarnings("serial")
public class Client extends JFrame implements ActionListener, Runnable {
	
	private static int portNumber = 5000;
    private Socket socket = null;
    private ObjectOutputStream outputStream = null;
    private ObjectInputStream inputStream = null;
    
    private Plotter dataPlotter;
    private JButton start, stop;
    private JTextField serverIPInput, sampleRateInput;
    private JLabel serverInfo;
	
	private boolean running;
    
    public Client() {
    	createGUI();
    }

    private void createGUI() {
    	JFrame frame = new JFrame("RPi Client");
    	this.dataPlotter = new Plotter();
    	this.serverIPInput = new JTextField(10);
    	this.sampleRateInput = new JTextField(10);
    	this.start = new JButton("Start");
    	this.stop = new JButton("Stop");
    	this.serverInfo = new JLabel("Server Info: ");
    	
    	this.start.addActionListener(this);
        this.stop.addActionListener(this);
    	
    	frame.getContentPane().setLayout(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.gridx = 0;
    	c.gridy = 0;
    	c.gridwidth = 2;
    	frame.add(this.dataPlotter, c);
    	
    	c.gridx = 0;
    	c.gridy = 1;
    	c.gridwidth = 2;
    	c.insets = new Insets(10, 0, 10, 0);
    	frame.add(new JSeparator(), c);
    	
    	c.gridx = 0;
    	c.gridy = 2;
    	c.gridwidth = 2;
    	frame.add(this.serverInfo, c);
    	
    	c.gridx = 0;
    	c.gridy = 3;
    	c.gridwidth = 2;
    	frame.add(new JSeparator(), c);
    	
    	c.gridx = 0;
    	c.gridy = 4;
    	c.gridwidth = 1;
    	frame.add(new JLabel("Enter server IP:"), c);
    	
    	c.gridx = 1;
    	c.gridy = 4;
    	frame.add(this.serverIPInput, c);
    	
    	c.gridx = 0;
    	c.gridy = 5;
    	frame.add(new JLabel("Enter sample rate (ms): "), c);
    	
    	c.gridx = 1;
    	c.gridy = 5;
    	frame.add(this.sampleRateInput, c);
    	
    	c.gridx = 0;
    	c.gridy = 6;
    	c.gridwidth = 2;
    	frame.add(this.start, c);
    	
    	c.gridx = 0;
    	c.gridy = 7;
    	frame.add(this.stop, c);
    	
    	frame.pack();
    	frame.setVisible(true);
    }
    
    private boolean connectToServer(String serverIP) {
    	try {
    		this.socket = new Socket(serverIP, portNumber);
    		this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
    		this.inputStream = new ObjectInputStream(this.socket.getInputStream());
    		System.out.println("Connected to server: " + this.socket.getInetAddress() 
    				+ " on port: " + this.socket.getPort());
    	} 
        catch (Exception e) {
        	System.out.println("Failed to connect to the server at port: " + portNumber);
        	System.out.println("Exception: " + e.toString());	
        	return false;
        }
		return true;
    }
	
    private void send(Object o) {
		try {
		    System.out.println("Sending an object...");
		    outputStream.writeObject(o);
		    outputStream.flush();
		} 
	    catch (Exception e) {
		    System.out.println("Exception occurred on sending: " +  e.toString());
		}
    }

    private Object receive() {
		Object o = null;
		try {
			System.out.println("About to receive an object...");
		    o = inputStream.readObject();
		    System.out.println("Object received");
		} 
	    catch (Exception e) {
		    System.out.println("Exception occurred on receiving: " + e.toString());
		}
		return o;
    }
    
    private TemperatureObject requestTemperature() {
    	TemperatureObject temperatureObject = new TemperatureObject();
    	send(temperatureObject);
    	temperatureObject = (TemperatureObject) receive();
    	System.out.println("Received: " + temperatureObject);
    	return temperatureObject;
    }
    
    @Override
	public void run() {
    	connectToServer(this.serverIPInput.getText());
    	
    	int sampleRate = Integer.parseInt(this.sampleRateInput.getText());
    	this.dataPlotter.clearDataPoints();
    	
    	while(this.running) {
    		TemperatureObject temperatureObject = requestTemperature();
    		this.dataPlotter.addDataPoint(temperatureObject.getTemperature());
    		try {
				Thread.sleep(sampleRate);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	
    	System.out.println("Thread ended");
	}
    
    @Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Start")) {
			if (this.serverIPInput.getText().length() > 0 && this.sampleRateInput.getText().length() > 0) {
				this.running = true;
				Thread thread = new Thread(this);
				thread.start();
			}
		}
		else if (e.getActionCommand().equals("Stop")) {
			try {
	            System.out.println("Closing down the client socket gracefully");
	            this.running = false;
	            this.socket.close();
	            this.outputStream.close();
	            this.inputStream.close();
	        }
	        catch (IOException exc) {
	            System.err.println("Could not close client socket: " + exc.getMessage());
	        }
			catch (NullPointerException exc) {
				System.err.println("No socket to close");
			}
		}
	}

    public static void main(String args[]) {	
    	new Client();
    }
}