/**
* Project Go-Kart Control / Go-Kart Panel
* @author	Gerd Bartelt - www.sebulli.com
*
* @copyright	GPL3
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/

// https://code.google.com/p/xbee-arduino/
#include <XBee.h>

// Address of the master module
#define ADDR_HIGH 0x0013A200
#define ADDR_LOW  0x40A28DB6

//#define ADDR_HIGH 0x00000000
//#define ADDR_LOW  0x0000FFFF

XBee xbee = XBee();

uint8_t txData[10] = { 0x55, 0xAA, 0, 0, 0, 0, 0, 0 };

// create reusable response objects for responses we expect to handle 
ZBRxResponse rx = ZBRxResponse();
ModemStatusResponse msr = ModemStatusResponse();

// SH + SL Address of master module
XBeeAddress64 addr64 = XBeeAddress64(ADDR_HIGH, ADDR_LOW);
ZBTxRequest zbTx = ZBTxRequest(addr64, txData, sizeof(txData));

uint8_t RFPower = 0;
uint8_t RFPowerOld = 0;

boolean sendBack = false;

// AT commands
uint8_t plCmd[] = {'P','L'};
uint8_t plVal[] = {'4'};
AtCommandRequest atRequest = AtCommandRequest(plCmd, plVal , sizeof(plVal));

// global variables
extern uint8_t shiftReg[4];


/**
 * Initialize the radio
 *
 */
void Radio_Init() {
  
  // Open the serial port to the XBee device
  Serial1.begin(9600);
  xbee.setSerial(Serial1);
  
  // Start with maximum power until we receive something
  Radio_SetMaxPwr();

}

/**
 * Radio task called in the main loop
 *
 */
void Radio_Task() {
  
    xbee.readPacket();
    
    if (xbee.getResponse().isAvailable()) {
      
      if (xbee.getResponse().getApiId() == ZB_RX_RESPONSE) {
        // got a zb rx packet



        // now fill our zb rx class
        xbee.getResponse().getZBRxResponse(rx);
        
        // is the length correct ?
        if (rx.getDataLength() == 10) {
          
          // is it a valid data packet ?
          if (  (rx.getData(0) == 0x55) && (rx.getData(1) == 0xAA) ) {
               
               // Reset timeoutcounter
               timeoutCnt = 0;
               
               // Get the display data
               shiftReg[0] = rx.getData(2);
               shiftReg[1] = rx.getData(3);
               shiftReg[2] = rx.getData(4);
               shiftReg[3] = rx.getData(5);
               
               // get the RFPower setpoint
               RFPower = rx.getData(6);
               
               // Get an new timeout value
               if (rx.getData(7) == 0) // 0 is not valid. Use at least 10sec
                 timeout = 100;
               else
                 timeout = 100 * (int)rx.getData(7);
                 
               sendBack = true;  
                 
          }
        }
      }
    } 
    // Some changes in RF power setpoint ?
    if (RFPower != RFPowerOld) {
        
        // Set the new power value
        if (RFPower <= 4) {
          plVal[0] = RFPower;
          atRequest.setCommand(plCmd);
          atRequest.setCommandValue(plVal); 
          atRequest.setCommandValueLength(sizeof(plVal)); 
          // send the AT command
          xbee.send(atRequest);
        }
        
        RFPowerOld = RFPower;
    }
    
    // Send a response back to the sender
    if (sendBack) {
      txData[2] = shiftReg[0];
      txData[3] = shiftReg[1];
      txData[4] = shiftReg[2];
      txData[5] = shiftReg[3];
      txData[7] = batteryValue;
      
      // Send the data now
      xbee.send(zbTx);
      sendBack = false;
    }
}

/**
 * Set the power to the maximum value
 *
 */
void Radio_SetMaxPwr() {
  RFPower = 4; // 4=316mW
}
