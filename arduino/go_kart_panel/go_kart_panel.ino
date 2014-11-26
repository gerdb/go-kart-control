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

// Firmware version will be displayed after reset
#define SW_VERSION 001

int timeoutCnt = 0;
int timeout = 100;
boolean isTimeout = false;

/**
 * The setup for the whole project
 *
 */
void setup() {

  Timer1.initialize(100000);
  Timer1.attachInterrupt(task100ms);
  
  // initialize the power module
  Power_Init();
  
  // initialize the display module
  Display_Init();
  
  // Restart the timeout after the initialization
  timeoutCnt = 0;
  

}


/**
 * The main loop
 *
 */
void loop() {
  Power_Task();
  Display_Task();
  
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
}

