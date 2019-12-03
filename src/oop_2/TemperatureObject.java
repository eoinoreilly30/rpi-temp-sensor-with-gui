package oop_2;

import java.io.Serializable;

public class TemperatureObject implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int temperature;
	private String serverName;
	private String dateTime;
	private int sampleNumber;

	public int getTemperature() {
		return temperature;
	}
	
	public void setTemperature(int temperature) {
		this.temperature = temperature;
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
	
	@Override
	public String toString() {
		return Integer.toString(this.temperature);
	}
}
