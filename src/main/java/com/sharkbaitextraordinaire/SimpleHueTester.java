package com.sharkbaitextraordinaire;

import java.util.List;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;

public class SimpleHueTester {
	
	private static PHHueSDK phHueSDK;

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
	
	public boolean connectToLastKnownAccessPoint() {
		String username = "";
		String lastIpAddress = "";
		
		if (username==null || lastIpAddress==null) {
			System.out.println("No information about last connection available");
			return false;
		}
		return true;
	}
	

	public static void main(String[] args) {
		
		SimpleHueTester tester = new SimpleHueTester();
		PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
		sm.search(true, true);
		
		phHueSDK.disableAllHeartbeat();
		phHueSDK.destroySDK();
		System.exit(0);
	}
	
}
