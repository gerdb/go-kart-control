package com.rapplogic.xbee;

import purejavacomm.SerialPortEvent;

public interface RxTxSerialEventListener {
	public void handleSerialEvent(SerialPortEvent event);
}
