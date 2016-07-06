package com.sharkbaitextraordinaire;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;

public class SimpleHueTester {
	
	private static PHHueSDK phHueSDK;
	private static Properties configProperties = new Properties();

	public SimpleHueTester() {
		phHueSDK = PHHueSDK.getInstance();
		phHueSDK.setAppName("SensorClient");
		phHueSDK.setDeviceName("SensorClient");
		phHueSDK.getNotificationManager().registerSDKListener(listener);
	}
	
	private PHSDKListener listener = new PHSDKListener() {

		public void onAccessPointsFound(List<PHAccessPoint> accessPointsList) {
			if (accessPointsList != null && accessPointsList.size() == 0 ) {
				System.out.println("no bridges found");
			} else if (accessPointsList != null && accessPointsList.size() == 1) {
				if (!connectToLastKnownAccessPoint()) {
					System.out.println("One bridge found");
					PHAccessPoint accessPoint = accessPointsList.get(0);
					System.out.println("Access Point IP: " + accessPoint.getIpAddress());
					System.out.println("AccessPoint bridge: " + accessPoint.getBridgeId());
	//				System.out.println("Connecting to acess point...");
	//				phHueSDK.connect(accessPoint);
	//				System.out.println("Connected to access point");
				}
			}
		}

		public void onAuthenticationRequired(PHAccessPoint accessPoint) {
			System.out.println("Go push the button on the bridge");
			PHHueSDK.getInstance().startPushlinkAuthentication(accessPoint);
		}

		public void onBridgeConnected(PHBridge bridge, String username) {
			System.out.println("Connected to bridge " + bridge + " as " + username);
			PHHueSDK.getInstance().setSelectedBridge(bridge);
			PHHueSDK.getInstance().enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
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
	
	public boolean connectToLastKnownAccessPoint() {
		String lastIpAddress = configProperties.getProperty("hue.last_connected_ip");
		String username = configProperties.getProperty("hue.username");
		
		if (username==null || lastIpAddress==null) {
			System.out.println("No information about last connection available");
			return false;
		}
        PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(lastIpAddress);
        accessPoint.setUsername(username);
        System.out.println("Connecting to " + accessPoint.getIpAddress() + " with username " + accessPoint.getUsername());
        phHueSDK.connect(accessPoint);
        return true;
	}
	

	public static void main(String[] args) {
		
		SimpleHueTester tester = new SimpleHueTester();
		
		if (!tester.connectToLastKnownAccessPoint()); {
			System.out.println("Something went wrong connecting");
			PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
			sm.search(true, true);
		}
		PHBridgeResourcesCache cache = phHueSDK.getSelectedBridge().getResourceCache();
		List<PHLight> lights = cache.getAllLights();
		
		for (PHLight light : lights) {
			System.out.println("Light named " + light.getName() + ": " + light.getLastKnownLightState());
		}
		
		phHueSDK.disableAllHeartbeat();
		phHueSDK.destroySDK();
		System.exit(0);
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
	
}
