package ca.etsmtl.capra.sensorsclient;

import javax.swing.JFrame;

import ca.etsmtl.capra.sensorsclient.communication.Communication;
import ca.etsmtl.capra.sensorsclient.communication.RealCommunication;

public class Main {
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//final Communication communication = new MockCommunication();
		final Communication communication = new RealCommunication("localhost", 53001);
		frame.add(new SensorsClientPanel(communication));
		
		frame.pack();
		frame.setVisible(true);
	}

}
