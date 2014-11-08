/**
 *  Project     Go-Kart Control
 *  @file		Communication.java
 *  @author		Gerd Bartelt - www.sebulli.com
 *  @brief		Communicate with the radio modules
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

public class Communication {
	
	CommPortIdentifier serialPortId;
	Enumeration<?> enumComm;
	SerialPort serialPort;
	OutputStream outputStream;
	// InputStream inputStream;
	Boolean serialPortGeoeffnet = false;

	int baudrate = 9600;
	int dataBits = SerialPort.DATABITS_8;
	int stopBits = SerialPort.STOPBITS_1;
	int parity = SerialPort.PARITY_NONE;
	String portName = "/dev/ttyUSB0";
	
	int secondsRuntime = 20;

	void schliesseSerialPort()
	{
		if ( serialPortGeoeffnet == true) {
			//T: Status message
			Logger.getInstance().log(_("Closing serial port"));
			serialPort.close();
			serialPortGeoeffnet = false;
		} else {
			//T: Status message
			Logger.getInstance().log(_("Serial port already closed"));
		}
	}
	
	void sendeSerialPort(String nachricht)
	{
		System.out.println("Sende: " + nachricht);
		if (serialPortGeoeffnet != true)
			return;
		try {
			outputStream.write(nachricht.getBytes());
		} catch (IOException e) {
			//T: Error message
			Logger.getInstance().log(_("Error while sending"));
		}
	}
	
	boolean oeffneSerialPort(String portName)
	{
		Boolean foundPort = false;
		if (serialPortGeoeffnet != false) {
			//T: Status message
			Logger.getInstance().log(_("Serial port already opened"));
			return false;
		}
		enumComm = CommPortIdentifier.getPortIdentifiers();
		while(enumComm.hasMoreElements()) {
			serialPortId = (CommPortIdentifier) enumComm.nextElement();
			//T: Status message
			Logger.getInstance().log(_("Serial ports found:") + " "+ serialPortId.getName());
			if (portName.contentEquals(serialPortId.getName())) {
				foundPort = true;
				break;
			}
		}
		if (foundPort != true) {
			//T: Status message
			Logger.getInstance().log(_("Serial port not found:") + " "+ portName);
			return false;
		}
		try {
			serialPort = (SerialPort) serialPortId.open("Öffnen und Senden", 500);
			//T: Status message
			Logger.getInstance().log(_("Opening serial port:" + " " + portName));
		} catch (PortInUseException e) {
			//T: Status message
			Logger.getInstance().log(_("Serial port is blocked."));
		}
		try {
			outputStream = serialPort.getOutputStream();
		} catch (IOException e) {
			//T: Status message
			Logger.getInstance().log(_("No access to output stream.") );
		}
/*
		try {
			inputStream = serialPort.getInputStream();
		} catch (IOException e) {
			System.out.println("Keinen Zugriff auf InputStream");
		}
		try {
			serialPort.addEventListener(new serialPortEventListener());
		} catch (TooManyListenersException e) {
			System.out.println("TooManyListenersException für Serialport");
		}
		serialPort.notifyOnDataAvailable(true);
*/
		try {
			serialPort.setSerialPortParams(baudrate, dataBits, stopBits, parity);
		} catch(UnsupportedCommOperationException e) {
			//T: Status message
			Logger.getInstance().log(_("Could not set port parameters.") );

		}
		
		serialPortGeoeffnet = true;
		return true;
	}
	
	public Communication() {
		
		//System.out.println(java.library.path);
	    CommPortIdentifier serialPortId;
	    //static CommPortIdentifier sSerialPortId;
	    Enumeration<?> enumComm;
	    //SerialPort serialPort;

	    enumComm = CommPortIdentifier.getPortIdentifiers();
	    while (enumComm.hasMoreElements()) {
	     	serialPortId = (CommPortIdentifier) enumComm.nextElement();
	     	if(serialPortId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
	     		
	    		oeffneSerialPort(portName);
	    		sendeSerialPort("Hallo");
	    	}
	    }
	}
	
	
}
