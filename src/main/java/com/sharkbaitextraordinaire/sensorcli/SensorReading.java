package com.sharkbaitextraordinaire.sensorcli;

public interface SensorReading {
	
	/** The type of sensor */
	String getType();
	
	/** The value of the sensor reading */
	Double getValue();
	
	/** The units of measurement */
	String getUnits();
}
