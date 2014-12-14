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

// Power setpoint from 0 to 4  (0=1mW, 1=23mW, 2=100mW, 3=158mW, 4=316mW)
#define POWER_SETPOINT 4

// Address of the master module
#define MASTER_ADDR_HIGH 0x0013A200
#define MASTER_ADDR_LOW  0x40A28DB6

XBee xbee = XBee();

uint8_t txData[10] = { 0x55, 0xAA, 0, 0, 0, 0, 0, 0 };

uint32_t myaddr_high = 0;
uint32_t myaddr_low =  0;

// create reusable response objects for responses we expect to handle 
ZBRxResponse rx = ZBRxResponse();
AtCommandResponse rxAT = AtCommandResponse();
ModemStatusResponse msr = ModemStatusResponse();

// SH + SL Address of master module
XBeeAddress64 addr64 = XBeeAddress64(MASTER_ADDR_HIGH, MASTER_ADDR_LOW);
XBeeAddress64 rxaddr64;
ZBTxRequest zbTx = ZBTxRequest(addr64, txData, sizeof(txData));

int RFPower = -1;


// AT commands
uint8_t plCmd[] = {'P','L'};
uint8_t plVal[] = {'4'};
uint8_t shCmd[] = {'S','H'};
uint8_t slCmd[] = {'S','L'};
uint8_t wrCmd[] = {'W','R'};
AtCommandRequest atPLSetRequest = AtCommandRequest(plCmd, plVal , sizeof(plVal));
AtCommandRequest atPLGetRequest = AtCommandRequest(plCmd);
AtCommandRequest atSHGetRequest = AtCommandRequest(shCmd);
AtCommandRequest atSLGetRequest = AtCommandRequest(slCmd);
AtCommandRequest atWRRequest    = AtCommandRequest(wrCmd);

// global variables
extern uint8_t shiftReg[4];

boolean sendWRRequest = false;

/**
 * Initialize the radio
 *
 */
void Radio_Init() { 
  

  
  // Open the serial port to the XBee device
  Serial1.begin(9600);
  
  // Set AP2 mode
  delay(1100);
  Serial1.print("+++");
  delay(1100);
  Serial1.print("ATAP 2\r");
  delay(200);
  Serial1.print("ATCN\r");
  delay(200);
  
  xbee.setSerial(Serial1);
  
  // Read the power settings
  xbee.send(atPLGetRequest);
  
  // Read the address
  xbee.send(atSHGetRequest);
  
  // Indicates "ready"
  if (!wasTimeoutReset)
    Display_BlinkAll();
     
}

/**
 * Radio task called in the main loop
 *
 */
void Radio_Task() {
  
    xbee.readPacket();
    
    if (xbee.getResponse().isAvailable()) {
      
      // Received an AT response
      if (xbee.getResponse().getApiId() == AT_RESPONSE) {
        xbee.getResponse().getAtCommandResponse(rxAT);
        if ((rxAT.getCommand()[0] == 'P') && (rxAT.getCommand()[1] == 'L')) {
          // not red yet
          if (RFPower == -1) {
            
            // What is the power value setting in the module?
            RFPower = rxAT.getValue()[0];
            
            // It differes from the setpoint
            if (RFPower != POWER_SETPOINT) {
              
              // So set the new value
              plVal[0] = POWER_SETPOINT;
              atPLSetRequest.setCommand(plCmd);
              atPLSetRequest.setCommandValue(plVal);
              atPLSetRequest.setCommandValueLength(sizeof(plVal));
              // send the AT command
              xbee.send(atPLSetRequest);
              sendWRRequest = true;
            }
          } else {
            
            // Send a WR to store the power setting
            if (sendWRRequest) {
              xbee.send(atWRRequest);
              sendWRRequest = false;
            }

          }  
          
        }
        
        // Get the serial number
        if (rxAT.getCommand()[0] == 'S') {
          
          // AT SH serial high
          if (rxAT.getCommand()[1] == 'H') {
            myaddr_high = Radio_GetLongVal(rxAT.getValue());
            xbee.send(atSLGetRequest);
          }

          // AT SL serial low
          if (rxAT.getCommand()[1] == 'L') {
            myaddr_low = Radio_GetLongVal(rxAT.getValue());
          }
        } 
        
      } else 
      // received data
      if (xbee.getResponse().getApiId() == ZB_RX_RESPONSE) {
        // got a zb rx packet
        
        // now fill our zb rx class
        xbee.getResponse().getZBRxResponse(rx);
        
        // Accept only packets from our master
        if (rx.getRemoteAddress64().getLsb() == MASTER_ADDR_LOW) {
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
               
    
              // Get an new timeout value
              if (rx.getData(7) < 10) // 0..9 is not valid. Use at least 100sec
                timeout = 1000;
              else
                timeout = 100 * (int)rx.getData(7);
              
              // Send back a response   
              txData[2] = shiftReg[0];
              txData[3] = shiftReg[1];
              txData[4] = shiftReg[2];
              txData[5] = shiftReg[3];
              txData[6] = 0;
              txData[7] = batteryValue;
      
              // Send the data now
              xbee.send(zbTx);
                 
            }
          }
        }
      }
    } 
    
}

/**
 * Gets a long (UINT32) value from the 4 received bytes
 *
 */
uint32_t Radio_GetLongVal(uint8_t* bytes) {
  int i;
  uint32_t val=0;
  for (i=0;i<4;i++) {
    val <<= 8;
    val |= bytes[i];
  }
  return val;
}


/**
 * Geter for the setpoint value
 *
 */
int Radio_GetPowerSetpoint() {
  return POWER_SETPOINT;
}
