package oop_2;

import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;

public class ThreadedConnectionHandler extends Thread {
    private Socket clientSocket = null;
    private ObjectInputStream inputStream = null;
    private ObjectOutputStream outputStream = null;
    private String serverName;

    public ThreadedConnectionHandler(Socket clientSocket, String serverName) {
        this.clientSocket = clientSocket;
        this.serverName = serverName;
    }

    @Override
    public void run() {
         try {
        	 // set up client I/O
            this.inputStream = new ObjectInputStream(clientSocket.getInputStream());
            this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            
            // begin receiving requests
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
		
		// flash led for 200 millseconds
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(path + "/trigger"));
			bw.write("none");
			bw.close();
			bw = new BufferedWriter(new FileWriter(path + "/brightness"));
			bw.write("1");
			bw.flush();
			Thread.sleep(200);
			bw.write("0");
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    private int readTemperature() {
    	String path = "/sys/class/thermal/thermal_zone0/temp";
    	int temperature = 0;
    	
    	// read temperature and divide by 1000 to get result in degrees celsius
    	try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String temperatureString = br.readLine();
			temperature = Integer.parseInt(temperatureString)/1000;
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return temperature;
    }
    
    private int readUtilization() {
    	String path = "/proc/stat";
    	int utilization = 0;
    	
    	try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			// read first line delimited by spaces
			String[] utilizationArray = br.readLine().split(" ");
			
			// sum up values
			int sum = 0;
			for (int i = 2; i < utilizationArray.length; i++) {
				sum += Integer.parseInt(utilizationArray[i]);
			}
			
			// divide idle time by total time, to get fraction of time spent idle
			int totalIdleTime = Integer.parseInt(utilizationArray[5])/sum;
			
			// get time spent not idle as percentage
			utilization = (1 - totalIdleTime)*100;
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return utilization;
    }
    
    private DataObject populateDataObject(DataObject dataObject, int sampleNumber) {
    	// number of threads minus 1 equals number of active clients
    	dataObject.setNumActiveClients(Thread.activeCount() - 1);

    	// set timestamp
    	DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
    	String formattedDateTime = LocalDateTime.now().format(dateTimeFormat);
    	dataObject.setDateTime(formattedDateTime);

    	dataObject.setServerName(this.serverName);

    	dataObject.setSampleNumber(sampleNumber);
    	
    	if (dataObject.getMonitorCPUTemp()) {
    		int reading = readTemperature();
    		System.out.println("CPU Temperature: " + reading + " @ " + formattedDateTime);
    		dataObject.setReading(reading);
    	}
    	else {
    		int reading = readUtilization();
    		System.out.println("CPU Utilization: " + reading + " @ " + formattedDateTime);
    		dataObject.setReading(reading);
    	}
    	
    	return dataObject;
    }

    private boolean receiveObject(int sampleNumber) {
        DataObject dataObject = null;

        try {
        	dataObject = (DataObject) inputStream.readObject();
        }
        catch (IOException e) {
        	System.out.println("There was a problem with the Input/Output Communication:");
        	this.closeSocket();
            return false;
        }
        catch (ClassNotFoundException e) {
        	System.out.println("The DataObject class could not be found");
        	this.closeSocket();
            return false;
		}
        
        dataObject = populateDataObject(dataObject, sampleNumber);
    	send(dataObject);

        return true;
    }

    private void send(Object o) {
        try {
            this.outputStream.writeObject(o);
            this.outputStream.flush();
            this.flashLED();
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
