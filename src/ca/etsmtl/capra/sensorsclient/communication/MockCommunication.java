package ca.etsmtl.capra.sensorsclient.communication;

import java.io.IOException;
import java.net.UnknownHostException;

public class MockCommunication implements Communication {

	@Override
	public String sendCommand(String command) throws UnknownHostException, IOException {
		if (command == "status")
			return "RangeFinder = 1\nCamera = 1\n";
		if (command == "battery-state")
			return "voltage: 0V\ncurrent: 0A";
		
		throw new RuntimeException("Unsupported command in MockCommunication.sendCommand");
	}

}
