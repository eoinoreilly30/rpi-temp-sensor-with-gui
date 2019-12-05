package oop_2;

import java.net.*;
import java.io.*;

public class ThreadedServer {
	private static int portNumber;
	public static String serverName;
	
	public ThreadedServer() {
		boolean listening = true;
        ServerSocket serverSocket = null;
        
        try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Server started on port: " + portNumber );
        }
        catch (IOException e) {
            System.out.println("Cannot listen on port: " + portNumber + ", Exception: " + e);
            System.exit(1);
        }
        
        while (listening) {
            Socket clientSocket = null;
            try {
            	System.out.println("Listening for a connection...");
                clientSocket = serverSocket.accept();
                System.out.println("Accepted socket connection from " + clientSocket.getInetAddress().toString());
                System.out.println("on port number: " + clientSocket.getPort());
            } 
            catch (IOException e) {
                System.out.println("Accept failed: " + portNumber + " " + e);
                listening = false;
            }	
            
            ThreadedConnectionHandler clientConnection = new ThreadedConnectionHandler(clientSocket);
            clientConnection.start(); 
            System.out.println("Finished communicating with client: " + clientSocket.getInetAddress().toString());
        }
        
        try {
            System.out.println("Closing down the server socket gracefully");
            serverSocket.close();
        }
        catch (IOException e) {
            System.err.println("Could not close server socket: " + e.getMessage());
        }
	}
	
	public static void main(String args[]) {
		try {
			ThreadedServer.serverName = args[0];
			ThreadedServer.portNumber = Integer.parseInt(args[1]);
		}
		catch (Exception e) {
			System.out.println("ERROR: Usage is: ThreadedServer [server name] [port number]");
			System.exit(1);
		}
		
		new ThreadedServer();
    }
}