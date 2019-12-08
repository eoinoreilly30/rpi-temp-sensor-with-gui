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

    private void flashLED() {
	String path = "/sys/class/leds/led0";
	BufferedWriter bw = new BufferedWriter(new FileWriter(path + "/trigger"));
	bw.write("none");
	bw.close();
	bw = new BufferedWriter (new FileWriter(path + "/brightness"));
	bw.write("1");
	Thread.sleep(200);
	bw.write("0");
	bw.close();
    }

    private boolean receiveObject(int sampleNumber) {
        DataObject dataObject = null;

        try {
        	dataObject = (DataObject) inputStream.readObject();

        	Random r = new Random();

        	if (dataObject.getMonitorCPUTemp()) {
        		int temperature = r.nextInt((90-60) + 1) + 60;
        		dataObject.setReading(temperature);
        	}
        	else {
        		int util = r.nextInt((30) + 1);
        		dataObject.setReading(util);
        	}

        	int numActiveClients = Thread.activeCount() - 1;
        	dataObject.setNumActiveClients(numActiveClients);
        	System.out.println("\nActive clients: " + numActiveClients);

        	DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
        	String formattedDateTime = LocalDateTime.now().format(dateTimeFormat);
        	dataObject.setDateTime(formattedDateTime);

        	dataObject.setServerName(ThreadedServer.serverName);

        	dataObject.setSampleNumber(sampleNumber);

        	System.out.println(dataObject.getMonitorCPUTemp() ? "CPU Temperature: " : "CPU Utilization: "
        			+ dataObject.getReading() + " @ " + formattedDateTime);
        	send(dataObject);
        }
        catch (Exception e) {
        	this.closeSocket();
            return false;
        }

        return true;
    }

    private void send(Object o) {
        try {
            this.outputStream.writeObject(o);
            this.outputStream.flush();
	    flashLED();
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
