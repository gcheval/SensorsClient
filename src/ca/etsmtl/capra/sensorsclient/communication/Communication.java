package ca.etsmtl.capra.sensorsclient.communication;

import java.io.IOException;
import java.net.UnknownHostException;

public interface Communication 
{
	public String sendCommand(String command) throws UnknownHostException, IOException ;
}
