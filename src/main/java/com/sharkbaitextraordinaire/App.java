package com.sharkbaitextraordinaire;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Sensor data (temperature/humidity) into colored lighting
 *
 */
public class App {
	
	private static Properties configProperties = new Properties();
	private static MqttClient client;
	private static MqttConnectOptions connectionOptions;
	
    public static void main( String[] args ) {
    	loadProperties(args[0]);
    	setUpMqttClient(configProperties);
    	
    	if(configProperties.getProperty("hue.last_connected_ip")==null 
    			|| configProperties.getProperty("hue.username")==null ) {
    		// We don't have an IP address or username to use to connect to the hue bridge,
    		// so set that up
    	} else {
    		// We should connect to the hue bridge
    	}
    }
    
    
    private static void loadDefaultProperties() {
    	String DEFAULT_PROPERTIES_FILE_NAME = "com/sharkbaitextraordinaire/sensorcli/default.properties";
    	InputStream in = App.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_FILE_NAME);
    	try {
    		configProperties.load(in);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    private static void loadProperties(String path) {
    	System.out.println("Using " + path + " as path to properties");
    	try {
    		FileInputStream fis = new FileInputStream(new File(path));
    		configProperties.load(fis);
    	} catch (FileNotFoundException e) {
    		System.err.println("Failed to load specified properties, loading default.properties");
    		loadDefaultProperties();
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    
	public static void setUpMqttClient(Properties properties) {
		String clientID = properties.getProperty("app.client.client_id");
		String brokerUrl = properties.getProperty("app.broker.url");
		
		connectionOptions = new MqttConnectOptions();
		connectionOptions.setUserName(properties.getProperty("app.client.username"));
		connectionOptions.setPassword(properties.getProperty("app.client.password").toCharArray());
		
		Properties sslProperties = new Properties();
		sslProperties.setProperty("com.ibm.ssl.protocol", properties.getProperty("com.ibm.ssl.protocol"));
		sslProperties.setProperty("com.ibm.ssl.trustStore", properties.getProperty("com.ibm.ssl.trustStore"));
		sslProperties.setProperty("com.ibm.ssl.trustStorePassword", properties.getProperty("com.ibm.ssl.trustStorePassword"));
		
		connectionOptions.setSSLProperties(sslProperties);
		
		try {
			client = new MqttClient(brokerUrl, clientID);
			client.setCallback(new MqttCallback() {
				public void connectionLost(Throwable cause) {
					System.out.println("connection to broker lost");
				}

				public void deliveryComplete(IMqttDeliveryToken token) {
					// no-op because we don't deliver
					
				}

				public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
					// This is where the meat of the operations will be
					String payload = new String(mqttMessage.getPayload());
					System.out.println(topic + ": " + payload);
				}
				
			});
			client.connect(connectionOptions);
		} catch (MqttException e) {
			System.out.println("not connected to broker");
			e.printStackTrace();
		}
		
		if (client.isConnected()) {
			System.out.println("Connected to mqtt broker at " + brokerUrl);
			String myTopic = properties.getProperty("app.broker.topic");
			try {
				int qos = 1;
				client.subscribe(myTopic, qos);
				System.out.println("Subscribing to " + myTopic);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
