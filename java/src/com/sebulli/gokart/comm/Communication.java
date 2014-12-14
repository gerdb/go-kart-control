/**
 *  Project     Go-Kart Control
 *  @author		Gerd Bartelt - www.sebulli.com
 *
 *  @copyright	GPL3
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.sebulli.gokart.comm;

import static com.sebulli.gokart.Translate._;

import java.util.ArrayList;
import java.util.List;

import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.AtCommand;
import com.rapplogic.xbee.api.AtCommandResponse;
import com.rapplogic.xbee.api.ErrorResponse;
import com.rapplogic.xbee.api.PacketListener;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.XBeeTimeoutException;
import com.rapplogic.xbee.api.wpan.RxResponse16;
import com.rapplogic.xbee.api.wpan.RxResponse64;
import com.rapplogic.xbee.api.wpan.TxRequest64;
import com.rapplogic.xbee.api.wpan.TxStatusResponse;
import com.rapplogic.xbee.api.zigbee.ZNetRxResponse;
import com.sebulli.gokart.Config;
import com.sebulli.gokart.Logger;

/**
 * Communicate with the radio modules
 *
 */
public class Communication {

	private boolean portOpened = false;
	private String portName = "";
	private int panels_amount = 1;
	private int panel_index = 1;
	private int rssi_panel_index = 0;
	private int communication_pause = 0;
	private int wait_counter = 0;
	private boolean newValues = false;
	private CommState commState;
	private ReceiveData rxdata;
	private List<XBeeAddress64> destAdresses;
	private List<String> panelNames;
	private List<Double> BatteryCorrection;
	private List<Boolean> Enabled;
	private boolean received = false;
	private int waitRxCnt = 0;
	private int repeat = 1;
	private int powerLevel = 0;
	private int repeatCnt = 0;
	static XBee xbee = new XBee();
	private int cycleTimeout;
	private int ncycleTimeout;
	private boolean toggle = false;
	private boolean doReset = false;

	/**
	 * Communication module Start the cyclic task
	 */
	public Communication() {

		// Get the port name
		portName = Config.getInstance().getProperty("port");

		// Amount of panels
		int amountGokartPanels = Config.getInstance().getPropertyAsInt("panels.amount");

		// Generate a object for received data
		rxdata = new ReceiveData(amountGokartPanels);

		destAdresses = new ArrayList<XBeeAddress64>();
		destAdresses.add(new XBeeAddress64());
		panelNames = new ArrayList<String>();
		panelNames.add("");
		BatteryCorrection = new ArrayList<Double>();
		BatteryCorrection.add(1.0);
		Enabled = new ArrayList<Boolean>();
		Enabled.add(true);
		for (int i = 1; i <= amountGokartPanels; i++) {
			destAdresses.add(new XBeeAddress64(FormatAddress(Config.getInstance().getProperty("panels." + i + ".serial"))));
			panelNames.add(Config.getInstance().getProperty("panels." + i + ".name"));
			BatteryCorrection.add(Config.getInstance().getPropertyAsDoubleIfExists("panels." + i + ".vcorr", 1.0));
			Enabled.add(Config.getInstance().getPropertyAsIntIfExists("panels." + i + ".enabled", 1) == 1);
		}
		
		// Read the parameter communication.pause and scale it to 100ms units
		communication_pause = (int) (Config.getInstance().getPropertyAsDouble("communication.pause") * 10.0);
		
		// Repeat n times
		repeat = Config.getInstance().getPropertyAsInt("communication.repeat");
		if (repeat<1)
			repeat = 1;

		powerLevel = Config.getInstance().getPropertyAsInt("communication.power");
		if (powerLevel<0)
			powerLevel = 0;
		if (powerLevel>4)
			powerLevel = 4;
		
		// Read the parameter communication.pause and scale it to 100ms units
		panels_amount = Config.getInstance().getPropertyAsInt("panels.amount");
		if (panels_amount < 1)
			// T: Error message
			Logger.getLogger().error(_("Parameter panels.amount must be set to at least 1."));

		// Get the timeout setting
		ncycleTimeout = Config.getInstance().getPropertyAsInt("communication.timeout");
		// Calculate the cycle timeout in 0.1s units
		cycleTimeout = (communication_pause + ( panels_amount * repeat * 10)) * ncycleTimeout;
		// scale it to 10s
		cycleTimeout = cycleTimeout / 100 + 1;
		// Limit it
		if (cycleTimeout < 1)
			cycleTimeout = 1;
		if (cycleTimeout > 255)
			cycleTimeout = 255;
		
		
		commState = CommState.Initialize;
	}

	private String FormatAddress(String s) {

		String ret = "";

		s = s.replace(" ", "");
		s = s.trim();
		if (s.length() != 16) {
			// T: Error log
			Logger.getLogger().error(_("Address must be 16 characters long:") + " " + s);
			s = "0000000000000000";
		}

		for (int i = 0; i < 8; i++) {
			if (!ret.isEmpty())
				ret += " ";
			if (s.length() > 2)
				ret += s.substring(0, 2);
			else
				ret += s;
			if (!s.isEmpty())
				s = s.substring(2);
		}

		return ret;
	}

	@SuppressWarnings("deprecation")
	public ReceiveData exchangeData(TransmitData txdata) {

		switch (commState) {

		case Initialize:
			panel_index = 1;
			commState = CommState.CycleStart;

			break;

			
		case CycleStart:
			
			if (portOpened) {
				if (Enabled.get(panel_index)) {
					commState = CommState.TransmitData;
					repeatCnt = 0;
				}
				else
					commState = CommState.NextPanel;
			}
			break;
			

		// Exchange the data
		case TransmitData:
			if (repeatCnt == 0)
				Logger.getLogger().logStart(panelNames.get(panel_index) + " ");
			Logger.getLogger().logMiddle(".");
			
			repeatCnt ++;
			
			rssi_panel_index = 0;
			received = false;
			
			int[] payload = new int[10];
			payload[0] = 0x55;
			payload[1] = 0xAA;
			payload[2] = txdata.getDisplayValue()[0]; // Display left
			payload[3] = txdata.getDisplayValue()[1]; // Display middle
			payload[4] = txdata.getDisplayValue()[2]; // Display right
			payload[5] = txdata.getFlagValue();       // Status LEDs
			payload[6] = 0; // reserved
			payload[7] = cycleTimeout; // Timeout in 10ms 
			payload[8] = 0; // reserved
			payload[9] = 0; // reserved

			// Get the remote XBee 64-bit address
			XBeeAddress64 destination = destAdresses.get(panel_index);

			TxRequest64 tx = new TxRequest64(destination, payload);

			TxStatusResponse status;
			try {
				status = (TxStatusResponse) xbee.sendSynchronous(tx);
				if (status.isSuccess()) {
					// Log message
					Logger.getLogger().debug("Data sent to XBee module:" + destination);
				} else {
					Logger.getLogger().debug("Error sending to XBee module:" + destination);
				}
			} catch (XBeeTimeoutException e) {
				Logger.getLogger().error("Tx: XBeeTimeoutException");
				
				// Reset the module, because of error during sending
				doReset = true;
			} catch (XBeeException e) {
				Logger.getLogger().error("Tx: XBeeException");
			}
			waitRxCnt = 0;
			commState = CommState.WaitRX;
			break;
	
		// Get the signal strength indicator
		case WaitRX:
			waitRxCnt ++;
			if (waitRxCnt >= 1) {
				commState = CommState.GetRSSI;
			}
			break;
			
		// Get the signal strength indicator
		case GetRSSI:

			// Try it again
			if ((repeatCnt < repeat) && !(received && (rssi_panel_index == panel_index)) && !doReset) {
				commState = CommState.TransmitData;
			} else {
				
				
				if (received && (rssi_panel_index == panel_index)) {
					
					// We received something, so get the RSSI value
					try {
						//xbee.sendSynchronous(new AtCommand("RC", 0));
						xbee.sendSynchronous(new AtCommand("DB"));
					} catch (XBeeTimeoutException e) {
						Logger.getLogger().error("DB: XBeeTimeoutException");
					} catch (XBeeException e) {
						Logger.getLogger().error("DB: XBeeException");
					}
					Logger.getLogger().logEnd("✓");
				} else {
					// No signal
					rxdata.setRSSIValue(panel_index, 0);
					Logger.getLogger().logEnd("?");
				}
				
				commState = CommState.NextPanel;
			}

			break;

		// Select the next panel
		case NextPanel:			
			// Next panel
			panel_index++;
			if ((panel_index > panels_amount) || doReset) {
				panel_index = 1;
				wait_counter = 0;
				commState = CommState.Wait;
			} else {
				commState = CommState.CycleStart;
			}
			break;

		// Make a break between 2 communication cycles and wait
		case Wait:
			
			wait_counter++;

			if (wait_counter > communication_pause || newValues) {
				newValues = false;
				
				// Is a reset necessary ?
				if (doReset)
					commState = CommState.Reset;
				else
					commState = CommState.Initialize;
			}

			break;
			
		// Reset the XBee module and restart it
		case Reset:
			try {
				Logger.getLogger().log(_("Resetting the XBee module"));
				xbee.reset();
				xbee.setAP2Mode();
				xbee.sendAtCommand(new AtCommand("PL", powerLevel));
				doReset = false;
			} catch (XBeeException e) {
				Logger.getLogger().error(_("Error resetting the XBee module"));
			}
			commState = CommState.Initialize;
			break;
				
				
		default:
			break;

		}

		return rxdata;
	}

	public void setNewValues() {
		newValues = true;
	}

	private void processXBeeResponse(XBeeResponse response) {
		if (response.isError()) {
			Logger.getLogger().log("response contains errors", ((ErrorResponse) response).getException());
		}

		if (response.getApiId() == ApiId.RX_16_RESPONSE) {
			Logger.getLogger().info("Received RX 16 packet " + ((RxResponse16) response));
		} else if (response.getApiId() == ApiId.RX_64_RESPONSE) {
			Logger.getLogger().info("Received RX 64 packet " + ((RxResponse64) response));
		} else if (response.getApiId() == ApiId.ZNET_RX_RESPONSE) {
			ZNetRxResponse  znetRxResponse = (ZNetRxResponse)response;
			XBeeAddress64 rxRemAddr64 = znetRxResponse.getRemoteAddress64();
			for (int i= 1; i<= panels_amount ; i++) {
				if (destAdresses.get(i).equals(rxRemAddr64)) {

					int rxDataBytes[] = znetRxResponse.getData();

					if (znetRxResponse.getData().length == 10) {

						if ((rxDataBytes[0] == 0x55) && (rxDataBytes[1] == 0xAA)) {
							rssi_panel_index = i;
							received = true;

							// Get 4 bytes that represent the 4 IO ports
							for (int ii = 0; ii<4; ii++)
								rxdata.setPortByte(i, ii, rxDataBytes[2 + ii]);

							// Get the battery voltage
							rxdata.setBattValue(i, ((float)rxDataBytes[7]) / 10 * BatteryCorrection.get(i));
							toggle = !toggle;
						}
						
					}
				}
			}

		} else if (response.getApiId() == ApiId.AT_RESPONSE) {
			AtCommandResponse  atCommandResponse = (AtCommandResponse)response;
			if (atCommandResponse.getCommand().equals("RC")) {
				rxdata.setRSSIValue(rssi_panel_index, - atCommandResponse.getValue()[0]);
			}
			if (atCommandResponse.getCommand().equals("DB")) {
				rxdata.setRSSIValue(rssi_panel_index, - atCommandResponse.getValue()[0]);
			}
		} else if (response.getApiId() == ApiId.TX_STATUS_RESPONSE) {
		} else {
			Logger.getLogger().info("Ignoring mystery packet " + response.toString());
		}
		


	}

	/**
	 * Getter for toggle variable
	 * 
	 * @return
	 */
	public boolean getToggle() {
		return toggle;
	}
	
	/**
	 * Opens the communication and initializes the XBee module
	 */
	@SuppressWarnings("deprecation")
	public void open() {

		try {
			xbee.open(portName, 9600);
			xbee.sendAtCommand(new AtCommand("PL", powerLevel));
			xbee.addPacketListener(new PacketListener() {

				@Override
				public void processResponse(XBeeResponse response) {
					processXBeeResponse(response);
				}
			});
			portOpened = true;
		} catch (Exception e2) {
			Logger.getLogger().error(e2);
		}

	}
	
	/**
	 * Close the communication
	 */
	public void close() {
		try {
			if (xbee.isConnected()) {
				xbee.close();
			}
		} catch (IllegalStateException e ) {
			System.exit(0);
		}
	}

	
	
}
