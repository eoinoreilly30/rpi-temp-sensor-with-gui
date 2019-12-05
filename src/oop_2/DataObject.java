package oop_2;

import java.io.Serializable;

public class DataObject implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int reading;
	private String serverName;
	private String dateTime;
	private int sampleNumber;
	private int numActiveClients;
	private boolean monitorCPUTemp;

	public int getReading() {
		return reading;
	}
	
	public void setReading(int reading) {
		this.reading = reading;
	}
	
	public String getServerName() {
		return serverName;
	}
	
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public String getDateTime() {
		return dateTime;
	}
	
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	
	public int getSampleNumber() {
		return sampleNumber;
	}
	
	public void setSampleNumber(int sampleNumber) {
		this.sampleNumber = sampleNumber;
	}
	
	public int getNumActiveClients() {
		return numActiveClients;
	}

	public void setNumActiveClients(int numActiveClients) {
		this.numActiveClients = numActiveClients;
	}
	
	public boolean getMonitorCPUTemp() {
		return monitorCPUTemp;
	}

	public void setMonitorCPUTemp(boolean monitorCPUTemp) {
		this.monitorCPUTemp = monitorCPUTemp;
	}
	
	@Override
	public String toString() {
		return "Sample Number: " + this.sampleNumber +
				(this.monitorCPUTemp ? "\nCPU Temperature: " : "\nCPU Utilization: ") + this.reading +
				"\nTime Stamp: " + this.dateTime + 
				"\nServer Name: " + this.serverName +
				"\nActive Clients: " + this.numActiveClients;
	}
}
