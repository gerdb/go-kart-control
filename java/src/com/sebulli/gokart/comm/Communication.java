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

import java.util.Enumeration;

import purejavacomm.CommPortIdentifier;
import purejavacomm.SerialPort;

import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress16;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeTimeoutException;
import com.rapplogic.xbee.api.wpan.TxRequest16;
import com.rapplogic.xbee.api.wpan.TxStatusResponse;
import com.sebulli.gokart.Logger;

/**
 * Communicate with the radio modules
 *
 */
public class Communication {

	CommPortIdentifier serialPortId;
	Enumeration<?> enumComm;
	SerialPort serialPort;

	String portName = "ttyUSB0";

	XBee xbee = new XBee();

	/**
	 * Send a text to the serial port
	 * 
	 * @param message
	 *            Text to send
	 */
	void sendSerialPort(String message) {
		System.out.println("Sending: " + message);
		
		// Note: we are using the Java int data type, since the byte data type is not unsigned, but the payload is limited to bytes.  That is, values must be between 0-255.
		int[] payload = new int[] { 90, 180 };

		// specify the remote XBee 16-bit MY address
		XBeeAddress16 destination = new XBeeAddress16(0x18, 0x74);

		TxRequest16 tx = new TxRequest16(destination, payload);

		TxStatusResponse status;
		try {
			status = (TxStatusResponse) xbee.sendSynchronous(tx);
			if (status.isSuccess()) {
				Logger.getLogger().log("geht");
			}
		} catch (XBeeTimeoutException e) {
			e.printStackTrace();
		} catch (XBeeException e) {
			e.printStackTrace();
		}
		

	}

	/**
	 * Communication module
	 */
	public Communication() {
		
	}
	
	public void open() {

		
		try {
			xbee.open(portName, 9600);
		} catch (XBeeException e1) {
			Logger.getLogger().error(_("Error initializing XBee module"), e1);
		} catch (Exception e2) {
			Logger.getLogger().error(_("Error opening port:" + " " + portName), e2);
			
			enumComm = CommPortIdentifier.getPortIdentifiers();
			while (enumComm.hasMoreElements()) {
				serialPortId = (CommPortIdentifier) enumComm.nextElement();
				// T: Status message
				Logger.getLogger().log(_("Serial ports found:") + " " + serialPortId.getName());
			}
		}
		
	}

}
