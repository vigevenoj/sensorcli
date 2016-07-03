package com.sharkbaitextraordinaire.sensorcli;

public class TemperatureSensorReading extends AbstractSensorReading {

	private static final String type = "temperature";
	
	public TemperatureSensorReading() {
		// The type is temperature
	}
	
	public String getType() {
		return type;
	}
}
