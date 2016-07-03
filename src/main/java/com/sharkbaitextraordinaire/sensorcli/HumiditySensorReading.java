package com.sharkbaitextraordinaire.sensorcli;

public class HumiditySensorReading extends AbstractSensorReading {

	private static final String type = "humidity";
	
	public HumiditySensorReading() {
		// type is humidity
	}
	
	public String getType() {
		return type;
	}
}
