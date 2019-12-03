package oop_2;

import java.net.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

@SuppressWarnings("serial")
public class Client extends JFrame implements ActionListener, Runnable {
	
	private static int portNumber = 5000;
    private Socket socket = null;
    private ObjectOutputStream outputStream = null;
    private ObjectInputStream inputStream = null;
    
    private Plotter dataPlotter;
    private JButton start, stop;
    private JTextField serverIPInput;
	private JTextField sampleRateInput;
	
	private Thread thread;
	private boolean running;
    
    public Client() {
    	createGUI();
    	this.thread = new Thread(this);
    }

    private boolean connectToServer(String serverIP) {
    	try {
    		this.socket = new Socket(serverIP, portNumber);
    		this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
    		this.inputStream = new ObjectInputStream(this.socket.getInputStream());
    		System.out.println("00. -> Connected to server:" + this.socket.getInetAddress() 
    				+ " on port: " + this.socket.getPort());
    		System.out.println("    -> from local address: " + this.socket.getLocalAddress() 
    				+ " and port: " + this.socket.getLocalPort());
    	} 
        catch (Exception e) {
        	System.out.println("XX. Failed to connect to the server at port: " + portNumber);
        	System.out.println("    Exception: " + e.toString());	
        	return false;
        }
		return true;
    }
	
    private void send(Object o) {
		try {
		    System.out.println("02. -> Sending an object...");
		    outputStream.writeObject(o);
		    outputStream.flush();
		} 
	    catch (Exception e) {
		    System.out.println("XX. Exception occurred on sending:" +  e.toString());
		}
    }

    private Object receive() {
		Object o = null;
		try {
			System.out.println("03. -- About to receive an object...");
		    o = inputStream.readObject();
		    System.out.println("04. <- Object received...");
		} 
	    catch (Exception e) {
		    System.out.println("XX. Exception occurred on receiving:" + e.toString());
		}
		return o;
    }
    
    private void createGUI() {
    	JFrame frame = new JFrame("RPi Client");
    	this.dataPlotter = new Plotter();
    	this.serverIPInput = new JTextField("Enter server IP", 10);
    	this.sampleRateInput = new JTextField("Enter sample rate", 10);
    	this.start = new JButton("Start");
    	this.stop = new JButton("Stop");
    	
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
    
    private TemperatureObject requestTemperature() {
    	TemperatureObject o = new TemperatureObject();
    	send(o);
    	return (TemperatureObject) receive();
    }
    
    @Override
	public void run() {
    	connectToServer(this.serverIPInput.getText());
    	int sampleRate = Integer.parseInt(this.sampleRateInput.getText());
    	
    	while(this.running) {
    		TemperatureObject temp = requestTemperature();
    		this.dataPlotter.addDataPoint(temp.getTemperature());
    		try {
				Thread.sleep(sampleRate);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
	}
    
    @Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Start")) {
			this.thread.start();
		}
		else if (e.getActionCommand().equals("Stop")) {
			try {
	            System.out.println("04. -- Closing down the client socket gracefully.");
	            this.socket.close();
	            this.outputStream.close();
	            this.inputStream.close();
	        }
	        catch (IOException exc) {
	            System.err.println("XX. Could not close client socket. " + exc.getMessage());
	        }
		}
	}

    public static void main(String args[]) {	
    	Client client = new Client();
    	
    	
    	System.out.println("**. End of Application.");
    }
}