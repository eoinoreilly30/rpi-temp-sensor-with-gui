package oop_2;

import java.net.*;
import java.io.*;

public class ThreadedConnectionHandler extends Thread {
    private Socket clientSocket = null;
    private ObjectInputStream inputStream = null;
    private ObjectOutputStream outputStream = null;
    
    public ThreadedConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
         try {
            this.inputStream = new ObjectInputStream(clientSocket.getInputStream());
            this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            while (this.readCommand()) {}
         } 
         catch (IOException e) 
         {
        	System.out.println("XX. There was a problem with the Input/Output Communication:");
            e.printStackTrace();
         }
    }

    private boolean readCommand() {
        String s = null;
        
        try {
            s = (String) inputStream.readObject();
        } 
        catch (Exception e) {
        	this.closeSocket();
            return false;
        }
        
        System.out.println("01. <- Received a String object from the client (" + s + ").");
        
        return true;
    }

    private void send(Object o) {
        try {
            System.out.println("02. -> Sending (" + o +") to the client.");
            this.outputStream.writeObject(o);
            this.outputStream.flush();
        } 
        catch (Exception e) {
            System.out.println("XX." + e.getStackTrace());
        }
    }
    
    public void sendError(String message) {
        this.send("Error:" + message);
    }
    
    public void closeSocket() {
        try {
            this.outputStream.close();
            this.inputStream.close();
            this.clientSocket.close();
        } 
        catch (Exception e) {
            System.out.println("XX. " + e.getStackTrace());
        }
    }
}