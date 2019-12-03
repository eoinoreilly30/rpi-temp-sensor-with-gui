package oop_2;

import java.net.*;
import java.util.Random;
import java.io.*;

public class ThreadedConnectionHandler extends Thread {
    private Socket clientSocket = null;
    private ObjectInputStream inputStream = null;
    private ObjectOutputStream outputStream = null;
    
    public ThreadedConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
         try {
            this.inputStream = new ObjectInputStream(clientSocket.getInputStream());
            this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
         } 
         catch (IOException e) {
        	System.out.println("There was a problem with the Input/Output Communication:");
            e.printStackTrace();
         }
         
         while (receiveObject()) {}
    }

    private boolean receiveObject() {
        TemperatureObject temp = null;
        
        try {
        	temp = (TemperatureObject) inputStream.readObject();
        	Random r = new Random();
        	temp.setTemperature(r.nextInt((90-30) + 1) + 30);
        	send(temp);
        } 
        catch (Exception e) {
        	this.closeSocket();
            return false;
        }
        
        System.out.println("Received an object from the client");
        
        return true;
    }

    private void send(Object o) {
        try {
            System.out.println("Sending (" + o +") to the client.");
            this.outputStream.writeObject(o);
            this.outputStream.flush();
        } 
        catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    public void closeSocket() {
        try {
        	System.out.println("Closing client socket");
            this.outputStream.close();
            this.inputStream.close();
            this.clientSocket.close();
        } 
        catch (Exception e) {
        	e.printStackTrace();
        }
    }
}