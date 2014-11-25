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


int VBAT_PIN = A5;     // analog input pin to read the battery voltage
int batteryValue = 0;  // battery voltage in 0.1V units


/**
 * Initialize the power module
 *
 */
void Power_Init() {
  Power_ReadVoltage();
}


/**
 * Read the battery voltage and scale it to 0.1Volts
 *
 */
void Power_ReadVoltage() {
  
  
  // Read the battery voltage
  // Vref of TPS54527 is 0.765V
  // Voltage divider is 124k/22k1, so the nominal output voltage is 5.0573V = AREF
  // Voltage divider R3/R4 = 10k/4k7, so a ADC voltage of AREF is caused by 15,8175V
  // So an ADC value of 1024 should be displayed as 158(.175)
  // Scale factor is 0.154468 = 5/32 - 1/512
  // 
  
  batteryValue = analogRead(VBAT_PIN);  
  batteryValue = batteryValue * 5 / 32 - batteryValue / 512;
}

/**
 * Power task called in the main loop
 *
 */
void Power_Task() {
  Power_ReadVoltage(); 
}
