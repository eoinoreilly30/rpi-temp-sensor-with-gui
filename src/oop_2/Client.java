package oop_2;

import java.net.*;
import java.io.*;

public class Client {
	
	private static int portNumber = 5000;
    private Socket socket = null;
    private ObjectOutputStream outputStream = null;
    private ObjectInputStream inputStream = null;

    public Client(String serverIP) {
    	if (!connectToServer(serverIP)) {
    		System.out.println("XX. Failed to open socket connection to: " + serverIP);            
    	}
    }

    private boolean connectToServer(String serverIP) {
    	try {
    		this.socket = new Socket(serverIP, portNumber);
    		this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
    		this.inputStream = new ObjectInputStream(this.socket.getInputStream());
    		System.out.println("00. -> Connected to Server:" + this.socket.getInetAddress() 
    				+ " on port: " + this.socket.getPort());
    		System.out.println("    -> from local address: " + this.socket.getLocalAddress() 
    				+ " and port: " + this.socket.getLocalPort());
    	} 
        catch (Exception e) {
        	System.out.println("XX. Failed to Connect to the Server at port: " + portNumber);
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
		    System.out.println("XX. Exception Occurred on Sending:" +  e.toString());
		}
    }

    private Object receive() 
    {
		Object o = null;
		try {
			System.out.println("03. -- About to receive an object...");
		    o = inputStream.readObject();
		    System.out.println("04. <- Object received...");
		} 
	    catch (Exception e) {
		    System.out.println("XX. Exception Occurred on Receiving:" + e.toString());
		}
		return o;
    }

    public static void main(String args[]) 
    {
//    	if (args.length == 1) {
//    		Client theApp = new Client(args[0]);
//		}
//    	else {
//    		System.out.println("Error: you must provide the address of the server");
//    		System.out.println("Usage is:  java Client x.x.x.x  (e.g. java Client 192.168.7.2)");
//    		System.out.println("      or:  java Client hostname (e.g. java Client localhost)");
//    	}
    	
    	
    	
    	
    	System.out.println("**. End of Application.");
    }
}