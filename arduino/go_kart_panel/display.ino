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

// 75HC595 shift register
int SDATA_Pin   = 11; 
int SCLK_Pin    = 12;
int SLATCH_Pin  = 13;

// Content of the 4 shift registers
uint8_t shiftReg[4] = {0,0,0,0};

// all 10 7-segment numbers
const uint8_t CODE7SEG[16] = {
  0b0111111, // 0
  0b0000110, // 1
  0b1011011, // 2
  0b1001111, // 3
  0b1100110, // 4
  0b1101101, // 5
  0b1111101, // 6
  0b0000111, // 7
  0b1111111, // 8
  0b1101111, // 9
  0b1110111, // A
  0b1111100, // B
  0b0111001, // C
  0b1011110, // D
  0b1111001, // E
  0b1110001  // F
};

extern int batteryValue;
extern boolean lowVoltage;
extern boolean isTimeout;
extern int timeoutCnt;

/**
 * Initialize the display
 *
 */
void Display_Init() {
    // initialize the ports as inputs or outputs.
  pinMode(SDATA_Pin, OUTPUT); 
  pinMode(SCLK_Pin, OUTPUT); 
  pinMode(SLATCH_Pin, OUTPUT); 
  
  digitalWrite(SDATA_Pin, LOW);
  digitalWrite(SCLK_Pin, LOW);
  digitalWrite(SLATCH_Pin, LOW);
  
  // Clear display
  Display_Clear();
  
  // Test the display
  Display_Test();
}

/**
 * Shift out 4 bytes to the 74HC595 register
 *
 */
void Display_WriteAll(void) {
  
  // switch off the display in low voltage condition
  if (lowVoltage || isTimeout) {
    Display_WriteByte(0);
    Display_WriteByte(0);
    Display_WriteByte(0);
    Display_WriteByte(0);
  }
  else {
    // Write data to the shift register in the order
    // how the shift registers are placed on the PCB
    Display_WriteByte(shiftReg[2]); // P3
    Display_WriteByte(shiftReg[0]); // P6
    Display_WriteByte(shiftReg[1]); // P5
    Display_WriteByte(shiftReg[3]); // P2
  }
   
  // Put the data to the outputs
  digitalWrite(SLATCH_Pin, HIGH);
  digitalWrite(SLATCH_Pin, LOW);
  
}

/**
 * Shift out one byte
 *
 */
void Display_WriteByte(uint8_t data) {
  shiftOut(SDATA_Pin, SCLK_Pin, MSBFIRST, data);
}

/**
 * Clear the display / switch off all segments
 *
 */
void Display_Clear() {
  shiftReg[0] = 0;
  shiftReg[1] = 0;
  shiftReg[2] = 0;
  shiftReg[3] = 0;
  
  Display_WriteAll();
}

/**
 * Show all segments
 *
 */
void Display_BlinkAll() {
  // Switch on each segment
  shiftReg[0] = 0xFF;
  shiftReg[1] = 0xFF;
  shiftReg[2] = 0xFF;
  shiftReg[3] = 0xFF;
  Display_WriteAll();
  delay(2000);

  Display_Clear();
}

/**
 * Display a number from 000 to 999
 *
 */
void Display_Number(int nr) {
  int i;
  
  // BCD
  for (i=2; i>=0; i--) {
    shiftReg[i] = CODE7SEG[nr % 10];
    nr /= 10;
  }
  
  shiftReg[3] = 0;
  Display_WriteAll();
}

/**
 * Display self test
 *
 */
void Display_Test(void) {
  
  Display_Clear();
  delay(500);
  
  // Show the battery voltage
  Power_ReadVoltage();
  Display_Number(batteryValue);
  delay(1000);
  
  Display_Clear();
  delay(500);

  // Show the version number
  Display_Number(SW_VERSION);
  delay(2000);

  Display_Clear();
  
}

/**
 * Display task called in the main loop
 *
 */
void Display_ShowAddr(uint32_t addr) {
  int i;

  Display_Clear();
  delay(1000);
  
  for (i=0; i<4; i++) {
    shiftReg[1] = CODE7SEG[(addr >> 28) & 0x0000000F];
    shiftReg[2] = CODE7SEG[(addr >> 24) & 0x0000000F];
    // Update the display 
    Display_WriteAll();
    addr <<= 8;
    delay(3000);
    Display_Clear();
    delay(500);
  }
}

/**
 * Display task called in the main loop
 *
 */
void Display_Task_100ms() {
   // Update the display 
   Display_WriteAll();
}
