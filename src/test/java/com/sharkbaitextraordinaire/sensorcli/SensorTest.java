package com.sharkbaitextraordinaire.sensorcli;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SensorTest {

	// Example messages from the sensors
//	topic=sensors/harold/feather/humidity,
//			message={"type": "humidity", "value" : 43.79 }
//	topic=sensors/harold/office/temperature, 
//	message={"type" : "temperature", "value" : 24.0 }
	
	String humidityMessage = "{\"type\" : \"humidity\",\"value\":43.79}";
	String temperatureMessage = "{\"type\" : \"temperature\", \"value\" : 24.0 }";
	
	@Test
	public void HumiditySensorTest() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		AbstractSensorReading humidityreading = mapper.readValue(humidityMessage, AbstractSensorReading.class);
		assertNotNull(humidityreading);
		assertTrue(humidityreading instanceof HumiditySensorReading);
		assertNotNull(humidityreading.getValue());
	}
	
	@Test
	public void TemperatureSensorTest() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		AbstractSensorReading temperaturereading = mapper.readValue(temperatureMessage, AbstractSensorReading.class);
		assertNotNull(temperaturereading);
		assertTrue(temperaturereading instanceof TemperatureSensorReading);
		assertNotNull(temperaturereading.getValue());
	}
}
