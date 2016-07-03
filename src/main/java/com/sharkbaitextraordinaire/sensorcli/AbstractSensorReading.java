package com.sharkbaitextraordinaire.sensorcli;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = TemperatureSensorReading.class, name = "temperature"),
	@JsonSubTypes.Type(value = HumiditySensorReading.class, name = "humidity")
})
public abstract class AbstractSensorReading implements SensorReading {

	private static String type;
	private String units;
	@JsonProperty
	private Double value;
	
	public String getType() {
		return type;
	}
	
	public Double getValue() {
		return value;
	}
	
	public void setValue(Double value) {
		this.value = value;
	}
	
	@JsonIgnore
	public String getUnits() {
		return units;
	}
	
	public void setUnits(String units) {
		this.units = units;
	}
}
