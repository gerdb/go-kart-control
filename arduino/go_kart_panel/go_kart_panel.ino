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

// http://playground.arduino.cc/uploads/Code/TimerOne.zip
#include "TimerOne.h"
#include <avr/wdt.h>

// Firmware version will be displayed after reset
#define SW_VERSION 003

int RESET_Pin  = 9;

int timeoutCnt = 0;
int timeout = 1000; // 0.1s steps (600 = 1min)
boolean isTimeout = false;
boolean is100msTick = false;
uint8_t mcusr = 0;
uint32_t magicCode __attribute__ ((section (".noinit"))); 
boolean wasTimeoutReset = false;
int rebootState = 0;
int rebootCnt = 0;
/**
 * The setup for the whole project
 *
 */
void setup() {
  
  pinMode(RESET_Pin, OUTPUT); 
  digitalWrite(RESET_Pin, HIGH);
   
  // Was it a timeout reset?
  wasTimeoutReset = (magicCode == 0xAA5500FF);
  magicCode = 0;
    
  Timer1.initialize(100000);
  Timer1.attachInterrupt(task100ms);
  
  // initialize the power module
  Power_Init();
  
  // initialize the display module
  Display_Init();
  
  // initialize the radio module
  Radio_Init();
  
  // Restart the timeout after the initialization
  timeoutCnt = 0;
  

}


/**
 * The main loop
 *
 */
void loop() {
  Power_Task();
  Radio_Task();
  
  // Generate a 100ms task
  if (is100msTick) {
    Display_Task_100ms();
    rebootTask();
    is100msTick = false;
  }
  
}

/**
 * The 100ms task
 *
 */
void task100ms() {

  // Timeout counter
  if (timeoutCnt < timeout) {
    timeoutCnt ++;
    isTimeout = false;
  }
  else {
    isTimeout = true;
  }

  is100msTick = true;
  
}

/*
 * State machine to reboot the system
 *
 */
void rebootTask() {
  
  // Reboot mechanism
  switch (rebootState) {
    
    // Wait
    case 0:
      if (isTimeout) {
        rebootState = 1;
        rebootCnt = 0;
      }
      break;

    // Reset the XBee module
    case 1:
      Display_Clear();
      digitalWrite(RESET_Pin, LOW);
      rebootCnt ++;
      if (rebootCnt > 5) {
        rebootState = 2;
        rebootCnt = 0;
      }
         
      break;
      
    // Reboot  
    case 2:
      digitalWrite(RESET_Pin, HIGH);
      magicCode = 0xAA5500FF;
      wdt_enable(WDTO_15MS);
      while(1);
         
      break;

   }
}
