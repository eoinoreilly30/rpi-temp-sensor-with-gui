package oop_2;

import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            
            int sampleNumber = 1;
            while (receiveObject(sampleNumber)) {sampleNumber++;}
         } 
         catch (IOException e) {
        	System.out.println("There was a problem with the Input/Output Communication:");
            e.printStackTrace();
            closeSocket();
         }
    }

    private boolean receiveObject(int sampleNumber) {
        TemperatureObject temperatureObject = null;
        
        try {
        	temperatureObject = (TemperatureObject) inputStream.readObject();
        	
        	Random r = new Random();
        	temperatureObject.setTemperature(r.nextInt((90-30) + 1) + 30);
        	
        	int numActiveClients = Thread.activeCount() - 1;
        	temperatureObject.setNumActiveClients(numActiveClients);
        	System.out.println("\nActive clients: " + numActiveClients);        	
        	
        	DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
        	String formattedDateTime = LocalDateTime.now().format(dateTimeFormat);
        	temperatureObject.setDateTime(formattedDateTime);
        	
        	temperatureObject.setServerName(InetAddress.getLocalHost().getHostName());
        	
        	temperatureObject.setSampleNumber(sampleNumber);
        	
        	send(temperatureObject);
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
        	System.out.println("Sending object to client");
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