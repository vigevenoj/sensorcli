package com.sharkbaitextraordinaire;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import com.sharkbaitextraordinaire.sensorcli.AbstractSensorReading;

/**
 * Sensor data (temperature/humidity) into colored lighting
 *
 */
public class App {
	
	private static Properties configProperties = new Properties();
	private static MqttClient client;
	private static MqttConnectOptions connectionOptions;
	private static PHHueSDK phHueSDK;
	
    public static void main( String[] args ) {
    	loadProperties(args[0]);
    	setUpMqttClient(configProperties);
    	
    	if(configProperties.getProperty("hue.last_connected_ip")==null 
    			|| configProperties.getProperty("hue.username")==null ) {
    		// We don't have an IP address or username to use to connect to the hue bridge,
    		// so set that up
    	} else {
    		// We should connect to the hue bridge
    		phHueSDK = PHHueSDK.getInstance();
    		phHueSDK.setAppName("SensorClient");
    		phHueSDK.setDeviceName("SensorClient");
    		phHueSDK.getNotificationManager().registerSDKListener(listener);
    		Runtime.getRuntime().addShutdownHook(shutdownHookThread);
    		connectToLastKnownAccessPoint();
    		PHBridgeResourcesCache cache = phHueSDK.getSelectedBridge().getResourceCache();
    		List<PHLight> myLights = cache.getAllLights();
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
					ObjectMapper mapper = new ObjectMapper();
					AbstractSensorReading reading = mapper.readValue(payload, AbstractSensorReading.class);
					// TODO update the latest sensor readings
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
	
	private static Thread shutdownHookThread = new Thread() {
		public void run() {
			System.out.println("Shutting down...");
			phHueSDK.disableAllHeartbeat();
			phHueSDK.destroySDK();
			System.out.println("Destroyed heartbeats and hue sdk objects");
		}
	};
	
	private static PHSDKListener listener = new PHSDKListener() {

		public void onAccessPointsFound(List<PHAccessPoint> accessPointsList) {
			if (accessPointsList != null && accessPointsList.size() == 0 ) {
				System.out.println("no bridges found");
			} else if (accessPointsList != null && accessPointsList.size() == 1) {
				System.out.println("One bridge found");
				PHAccessPoint accessPoint = accessPointsList.get(0);
				System.out.println("Access Point IP: " + accessPoint.getIpAddress());
				System.out.println("AccessPoint bridge: " + accessPoint.getBridgeId());
				System.out.println("Connecting to acess point...");
				phHueSDK.connect(accessPoint);
				System.out.println("Connected to access point");
			}
		}

		public void onAuthenticationRequired(PHAccessPoint accessPoint) {
			System.out.println("Go push the button on the bridge");
			PHHueSDK.getInstance().startPushlinkAuthentication(accessPoint);
		}

		public void onBridgeConnected(PHBridge bridge, String username) {
			PHHueSDK.getInstance().setSelectedBridge(bridge);
//			PHHueSDK.getInstance().enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
			String lastIpAddress = bridge.getResourceCache().getBridgeConfiguration().getIpAddress();
			System.out.println("username is :" + username);
			System.out.println("ip address  :" + lastIpAddress);
		}

		public void onCacheUpdated(List<Integer> arg0, PHBridge arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onConnectionLost(PHAccessPoint arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onConnectionResumed(PHBridge arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onError(int code, String message) {
			if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
				System.out.println("Bridge not responding");
				System.out.println(message);
			} else if (code == PHMessageType.PUSHLINK_BUTTON_NOT_PRESSED) {
				System.out.println("Did you push the pushlink button?");
				System.out.println(message);
			} else if (code == PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
				System.out.println("Authenticating to bridge failed");
				System.out.println(message);
			} else if (code == PHMessageType.BRIDGE_NOT_FOUND) {
				System.out.println("Couldn't find the bridge");
				System.out.println(message);
			}
			else System.out.println(message);
		}

		public void onParsingErrors(List<PHHueParsingError> parsingErrorsList) {
			for (PHHueParsingError parsingError : parsingErrorsList) {
				System.out.println("ParsingError: " + parsingError.getMessage());
			}
		}
		
	};
	
	public static boolean connectToLastKnownAccessPoint() {
		String username = configProperties.getProperty("hue.last_connected_ip");
		String lastIpAddress = configProperties.getProperty("hue.username");
		
		if (username==null || lastIpAddress==null) {
			System.out.println("No information about last connection available");
			return false;
		}
        PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(lastIpAddress);
        accessPoint.setUsername(username);
        phHueSDK.connect(accessPoint);
        return true;
	}
}
