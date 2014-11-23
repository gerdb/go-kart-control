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

#define VERSION_MAJOR 0
#define VERSION_MINOR 0
#define VERSION_PATCH 1

// 75HC595 shift register
int SDATA_Pin   = 11; 
int SCLK_Pin    = 12;
int SLATCH_Pin  = 13;



uint8_t shiftReg[4] = {0,0,0,0};
uint8_t code7seg[10] = {
  0b0111111, // 0
  0b0000110, // 1
  0b1011011, // 2
  0b1001111, // 3
  0b1100110, // 4
  0b1101101, // 5
  0b1111101, // 6
  0b0000111, // 7
  0b1111111, // 8
  0b1101111  // 9
};

/**
 * The setup
 *
 */
void setup() {                
  
  // initialize the ports as inputs or outputs.
  pinMode(SDATA_Pin, OUTPUT); 
  pinMode(SCLK_Pin, OUTPUT); 
  pinMode(SLATCH_Pin, OUTPUT); 
  
  digitalWrite(SDATA_Pin, LOW);
  digitalWrite(SCLK_Pin, LOW);
  digitalWrite(SLATCH_Pin, LOW);
  
  // Clear display
  writeShiftRegistersAll();
  // Test the display
  displayTest();
  
}

void writeShiftRegistersAll(void) {
  
  // Write data to the shift register in the order
  // how the shift registers are placed on the PCB
  writeShiftRegistersByte(shiftReg[2]); // P3
  writeShiftRegistersByte(shiftReg[0]); // P6
  writeShiftRegistersByte(shiftReg[1]); // P5
  writeShiftRegistersByte(shiftReg[3]); // P2
   
  // Put the data to the outputs
  digitalWrite(SLATCH_Pin, HIGH);
  digitalWrite(SLATCH_Pin, LOW);
  
}

void writeShiftRegistersByte(uint8_t data) {
  int i;
  
  for (i=0; i<8; i++) {
      
    // Set the SDATA output corresponding on the bit value
    if (data & 0x80)
      digitalWrite(SDATA_Pin, HIGH);
    else
      digitalWrite(SDATA_Pin, LOW);
      
    //Clock
    digitalWrite(SCLK_Pin, HIGH);
    digitalWrite(SCLK_Pin, LOW);
      
    // next bit
    data <<= 1;
  } 

}

void displayTest(void) {
  int i,ii;

  delay(1000);               // wait one second
  shiftReg[0] = 0;
  shiftReg[1] = 0;
  shiftReg[2] = 0;
  shiftReg[3] = 0;
  
  for (i=0; i<4; i++) {
    shiftReg[i] = 1;
    
    for (ii=0; ii<8; ii++) {
       writeShiftRegistersAll();
      delay(200);
      shiftReg[i] <<= 1;
    }
    shiftReg[i] = 0;
  }
  
  delay(500);
  
  shiftReg[0] = 0xFF;
  shiftReg[1] = 0xFF;
  shiftReg[2] = 0xFF;
  shiftReg[3] = 0xFF;
  writeShiftRegistersAll();
  
  delay(2000);

  shiftReg[0] = 0;
  shiftReg[1] = 0;
  shiftReg[2] = 0;
  shiftReg[3] = 0;
  writeShiftRegistersAll();
  
  delay(1000);

  shiftReg[0] = code7seg[VERSION_MAJOR];
  shiftReg[1] = code7seg[VERSION_MINOR];
  shiftReg[2] = code7seg[VERSION_PATCH];
  shiftReg[3] = 0;
  writeShiftRegistersAll();
  
  delay(2000);

  shiftReg[0] = 0;
  shiftReg[1] = 0;
  shiftReg[2] = 0;
  shiftReg[3] = 0;
  writeShiftRegistersAll();
  
  
}

// the loop routine runs over and over again forever:
void loop() {

}


