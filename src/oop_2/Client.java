package oop_2;

import java.net.*;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
    private MovingAveragePlotter movingAveragePlotter;
    private JButton start, stop;
    private JTextField serverIPInput, sampleRateInput;
    private JTextArea serverInfo;
    private JRadioButton cpuUtilization, cpuTemperature;
	
	private boolean running;
	private boolean monitorCPUTemp;

    public Client() {
    	JFrame frame = new JFrame("RPi Client");
    	this.dataPlotter = new Plotter();
    	this.movingAveragePlotter = new MovingAveragePlotter();
    	this.serverIPInput = new JTextField("192.168.8.194", 10);
    	this.sampleRateInput = new JTextField("200", 10);
    	this.start = new JButton("Start");
    	this.stop = new JButton("Stop");
    	this.serverInfo = new JTextArea("Connect to a server to view info...", 5, 0);
    	this.serverInfo.setEditable(false);
    	this.cpuTemperature = new JRadioButton("CPU Temperature", true);
    	this.cpuUtilization = new JRadioButton("CPU Utilization", false);
    	ButtonGroup buttonGroup = new ButtonGroup();
    	buttonGroup.add(this.cpuTemperature);
    	buttonGroup.add(this.cpuUtilization);
    	this.monitorCPUTemp = true;
    	
    	this.start.addActionListener(this);
        this.stop.addActionListener(this);
        this.cpuTemperature.addActionListener(this);
        this.cpuUtilization.addActionListener(this);
        
    	frame.getContentPane().setLayout(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	
    	c.insets = new Insets(5, 5, 5, 5);
    	c.fill = GridBagConstraints.HORIZONTAL;
    	
    	c.gridx = 0;
    	c.gridy = 0;
    	c.gridwidth = 3;
    	frame.add(new JLabel("Last 20 Readings"), c);
    	
    	c.gridx = 0;
    	c.gridy = 1;
    	frame.add(this.dataPlotter, c);
    	
    	c.gridx = 0;
    	c.gridy = 2;
    	frame.add(new JLabel("Moving Average"), c);
    	
    	c.gridx = 0;
    	c.gridy = 3;
    	frame.add(this.movingAveragePlotter, c);
    	
    	c.gridx = 0;
    	c.gridy = 4;
    	c.gridwidth = 1;
    	frame.add(new JLabel("Server Info: "), c);
    	
    	c.gridx = 1;
    	c.gridy = 4;
    	c.gridwidth = 2;
    	frame.add(this.serverInfo, c);
    	
    	c.gridx = 0;
    	c.gridy = 5;
    	c.gridwidth = 3;
    	frame.add(new JSeparator(), c);
    	
    	c.gridx = 0;
    	c.gridy = 6;
    	c.gridwidth = 1;
    	frame.add(new JLabel("Enter server IP:"), c);
    	
    	c.gridx = 1;
    	c.gridy = 6;
    	c.gridwidth = 2;
    	frame.add(this.serverIPInput, c);
    	
    	c.gridx = 0;
    	c.gridy = 7;
    	c.gridwidth = 1;
    	frame.add(new JLabel("Stat to Monitor: "), c);
    	
    	c.gridx = 1;
    	c.gridy = 7;
    	frame.add(this.cpuTemperature, c);
    	
    	c.gridx = 2;
    	c.gridy = 7;
    	frame.add(this.cpuUtilization, c);
    	
    	c.gridx = 0;
    	c.gridy = 8;
    	frame.add(new JLabel("Enter sample rate (ms): "), c);
    	
    	c.gridx = 1;
    	c.gridy = 8;
    	c.gridwidth = 2;
    	frame.add(this.sampleRateInput, c);
    	
    	c.gridx = 0;
    	c.gridy = 9;
    	c.gridwidth = 3;
    	frame.add(this.start, c);
    	
    	c.gridx = 0;
    	c.gridy = 10;
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
    
    private DataObject requestReading() {
    	DataObject dataObject = new DataObject();
    	dataObject.setMonitorCPUTemp(this.monitorCPUTemp);
    	send(dataObject);
    	dataObject = (DataObject) receive();
    	System.out.println("Received: " + dataObject);
    	return dataObject;
    }
    
    @Override
	public void run() {
    	connectToServer(this.serverIPInput.getText());
    	
    	int sampleRate = Integer.parseInt(this.sampleRateInput.getText());
    	this.dataPlotter.clearDataPoints();
    	this.movingAveragePlotter.clearDataPoints();
    	
    	while(this.running) {
    		DataObject dataObject = requestReading();
    		this.dataPlotter.addDataPoint(dataObject.getReading());
    		this.movingAveragePlotter.addDataPoint(dataObject.getReading());
    		this.serverInfo.setText(dataObject.toString());
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
			if (this.serverIPInput.getText().length() > 0
					&& this.sampleRateInput.getText().length() > 0) {
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
		else if (e.getActionCommand().equals("CPU Temperature")) {
			this.monitorCPUTemp = true;
			this.dataPlotter.clearDataPoints();
	    	this.movingAveragePlotter.clearDataPoints();
		}
		else if (e.getActionCommand().equals("CPU Utilization")) {
			this.monitorCPUTemp = false;
			this.dataPlotter.clearDataPoints();
	    	this.movingAveragePlotter.clearDataPoints();
		}
	}

    public static void main(String args[]) {	
    	new Client();
    }
}