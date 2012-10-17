package ca.etsmtl.capra.sensorsclient.communication;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class RealCommunication implements Communication {

	private final String host;
	private final int port;
	
	public RealCommunication(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	@Override
	public String sendCommand(String command) throws UnknownHostException, IOException {
		// connecting
		Socket socket = new Socket(host, port);
		
		// sending the command
		socket.getOutputStream().write(command.getBytes());
		socket.getOutputStream().flush();
		
		// reading
		StringBuilder sb = new StringBuilder();
		Scanner scan = new Scanner(socket.getInputStream());
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			sb.append(line);
			sb.append("\n");
		}
		
		// closing the socket 
		socket.close();
		
		return sb.toString();
	}

}
