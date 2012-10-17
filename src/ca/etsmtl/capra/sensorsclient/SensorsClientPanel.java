package ca.etsmtl.capra.sensorsclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import ca.etsmtl.capra.sensorsclient.communication.Communication;

public class SensorsClientPanel extends JPanel {

	private static final long serialVersionUID = 7677152295076478920L;
	
	private static final String MIG_LINE_CONSTRAINT = "wrap, w 300";
	private final Communication communication;
	

	private Vector<SensorCheckBox> checkboxes = new Vector<SensorCheckBox>();
	private JLabel batteryStateLabel = new JLabel();
	private JLabel connectionStatus = new JLabel();

	public SensorsClientPanel(Communication communication) {
		this.communication = communication;
		setupUi();
	}

	public void setupUi() {
		this.setLayout(new MigLayout());
		
		this.setBorder(new TitledBorder("Sensors:"));
		
		addConnectionStatus();
		addRefreshButton();
		addBetteriesStatus();
		addCheckBox("Lumiere");
		addCheckBox("Camera");
		addCheckBox("GPS");
		addCheckBox("IMU");
		addCheckBox("RangeFinder");
		addCheckBox("Moteurs");
		addCheckBox("S6");
		addCheckBox("S7");
		
		update();
	}

	
	private void addConnectionStatus() {
		connectionStatus.setText("Connection status...");
		this.add(connectionStatus, MIG_LINE_CONSTRAINT);
	}

	private void addRefreshButton() {
		JButton refreshButton = new JButton("Refresh");
		refreshButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				update();
			}
		});
		this.add(refreshButton, MIG_LINE_CONSTRAINT);
	}
	

	private void addBetteriesStatus() {
		batteryStateLabel.setText("<HTML><BR><BR><BR><HTML>");
		this.add(batteryStateLabel, MIG_LINE_CONSTRAINT);
	}
	
	private void addCheckBox(String title) {
		SensorCheckBox checkbox = new SensorCheckBox(title, communication);
		this.add(checkbox, MIG_LINE_CONSTRAINT);
		checkboxes.add(checkbox);
	}
	
	
	// Logic to update the information
	private void update() {
		try {
			updateCheckboxes();
			updateBatteryState();
			connectionStatus.setText("Refresh ended succesfully");
		}
		catch (Exception e) {
			signalConnectionError();
		}
	}
	
	private void updateCheckboxes() throws UnknownHostException, IOException {
		final String status = communication.sendCommand("status");
		final Hashtable<String, Boolean> sensors = toHashtable(status);				
		
		for (SensorCheckBox checkbox : checkboxes) {
			final String title = checkbox.getTitle();
			if (sensors.containsKey(title)) {
				checkbox.setSelected(sensors.get(title));
			}
			else {
				checkbox.setSelected(false);
			}
		}
	}
	
	private void updateBatteryState() throws UnknownHostException, IOException {
		final String batteryStatus = communication.sendCommand("battery-state");
		
		batteryStateLabel.setText("<HTML>" + batteryStatus.replace("\n", "<BR>") + "</HTML>");
	}
	
	private Hashtable<String, Boolean> toHashtable(String status) {
		Hashtable<String, Boolean> sensors = new Hashtable<String, Boolean>();
		for (String line : status.split("\n")) {
			final String[] parts = line.split("=");
			final String sensor = parts[0].trim();
			final Boolean enabled = parts[1].trim().equals("1");
			
			sensors.put(sensor, enabled);
		}
		
		return sensors;
	}
	
	
	/** 
	 * Definition of our SencorCheckBox
	 */
	private class SensorCheckBox extends JCheckBox
	{
		private static final long serialVersionUID = 7836151051182816174L;
		
		private final String title; 
		
		public SensorCheckBox(String title, Communication communication) {
			super(title);
			this.title = title;
			
			this.addActionListener(new CheckBoxClickHandler(this, communication));
		}
		
		public String getTitle() {
			return title;
		}
		
	}
	
	boolean updatingCheckbox = false;
	private class CheckBoxClickHandler implements ActionListener
	{
		private final SensorCheckBox checkbox;
		private final Communication communication;
		
		public CheckBoxClickHandler(SensorCheckBox checkbox, Communication communication)
		{
			super();
			this.checkbox = checkbox;
			this.communication = communication;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// wait if we are update checkboxes states
			if (updatingCheckbox) 
				return;
				
			String title = checkbox.getTitle();
			
			try {
				if (checkbox.isSelected()) {
					communication.sendCommand("start " + title);
				}
				else {
					communication.sendCommand("stop " + title);
				}	
			}
			catch (Exception e) {
				signalConnectionError();
			}
			
			updatingCheckbox = true;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			update();
			updatingCheckbox = false;
		}

	}
	
	private void signalConnectionError() {
		connectionStatus.setText("Impossible to connect to the sensors server");
	}
	

}
