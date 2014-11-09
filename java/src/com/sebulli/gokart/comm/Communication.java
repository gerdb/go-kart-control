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

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import com.sebulli.gokart.Logger;
import static com.sebulli.gokart.Translate._;

/**
 * Communicate with the radio modules
 *
 */
public class Communication {

	CommPortIdentifier serialPortId;
	Enumeration<?> enumComm;
	SerialPort serialPort;
	OutputStream outputStream;
	// InputStream inputStream;
	Boolean serialPortOpened = false;

	int baudrate = 9600;
	int dataBits = SerialPort.DATABITS_8;
	int stopBits = SerialPort.STOPBITS_1;
	int parity = SerialPort.PARITY_NONE;
	String portName = "/dev/ttyUSB0";

	int secondsRuntime = 20;

	/**
	 * Close the serial port
	 */
	void closeSerialPort() {
		if (serialPortOpened == true) {
			// T: Status message
			Logger.getInstance().log(_("Closing serial port"));
			serialPort.close();
			serialPortOpened = false;
		} else {
			// T: Status message
			Logger.getInstance().log(_("Serial port already closed"));
		}
	}

	/**
	 * Send a text to the serial port
	 * 
	 * @param message
	 *            Text to send
	 */
	void sendSerialPort(String message) {
		System.out.println("Sending: " + message);
		if (serialPortOpened != true)
			return;
		try {
			outputStream.write(message.getBytes());
		} catch (IOException e) {
			// T: Error message
			Logger.getInstance().log(_("Error while sending"));
		}
	}

	/**
	 * Open a serial port
	 * 
	 * @param portName
	 *            Name of the serial port
	 * @return True, if successful
	 */
	boolean openSerialPort(String portName) {
		Boolean foundPort = false;
		if (serialPortOpened != false) {
			// T: Status message
			Logger.getInstance().log(_("Serial port already opened"));
			return false;
		}
		enumComm = CommPortIdentifier.getPortIdentifiers();
		while (enumComm.hasMoreElements()) {
			serialPortId = (CommPortIdentifier) enumComm.nextElement();
			// T: Status message
			Logger.getInstance().log(_("Serial ports found:") + " " + serialPortId.getName());
			if (portName.contentEquals(serialPortId.getName())) {
				foundPort = true;
				break;
			}
		}
		if (foundPort != true) {
			// T: Status message
			Logger.getInstance().log(_("Serial port not found:") + " " + portName);
			return false;
		}
		try {
			serialPort = (SerialPort) serialPortId.open("Öffnen und Senden", 500);
			// T: Status message
			Logger.getInstance().log(_("Opening serial port:" + " " + portName));
		} catch (PortInUseException e) {
			// T: Status message
			Logger.getInstance().log(_("Serial port is blocked"));
		}
		try {
			outputStream = serialPort.getOutputStream();
		} catch (IOException e) {
			// T: Status message
			Logger.getInstance().log(_("No access to output stream"));
		}
		/*
		 * try { inputStream = serialPort.getInputStream(); } catch (IOException
		 * e) { System.out.println("No access to input stream"); } try {
		 * serialPort.addEventListener(new serialPortEventListener()); } catch
		 * (TooManyListenersException e) {
		 * System.out.println("TooManyListenersException for serial port"); }
		 * serialPort.notifyOnDataAvailable(true);
		 */
		try {
			serialPort.setSerialPortParams(baudrate, dataBits, stopBits, parity);
		} catch (UnsupportedCommOperationException e) {
			// T: Status message
			Logger.getInstance().log(_("Could not set port parameters"));

		}

		serialPortOpened = true;
		return true;
	}

	/**
	 * Communication module
	 */
	public Communication() {

		// System.out.println(java.library.path);
		CommPortIdentifier serialPortId;
		// static CommPortIdentifier sSerialPortId;
		Enumeration<?> enumComm;
		// SerialPort serialPort;

		enumComm = CommPortIdentifier.getPortIdentifiers();
		while (enumComm.hasMoreElements()) {
			serialPortId = (CommPortIdentifier) enumComm.nextElement();
			if (serialPortId.getPortType() == CommPortIdentifier.PORT_SERIAL) {

				openSerialPort(portName);
				sendSerialPort("Hello World");
			}
		}
	}

}
