/**
 *  Project     Go-Kart Control
 *  @file		LookUp7Segment.java
 *  @author		Gerd Bartelt - www.sebulli.com
 *  @brief		Lookup table for a 7 segment alphanumeric display
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


package com.sebulli.gokart.gui;

public class LookUp7Segment {

	private final static byte[] table ={
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
			0b0000000, // 10
			0b0000000, // 11
			0b0000000, // 12
			0b0000000, // 13
			0b0000000, // 14
			0b0000000, // 15
			0b0000000, // 16
			0b0000000, // 17
			0b0000000, // 18
			0b0000000, // 19
			0b0000000, // 20
			0b0000000, // 21
			0b0000000, // 22
			0b0000000, // 23
			0b0000000, // 24
			0b0000000, // 25
			0b0000000, // 26
			0b0000000, // 27
			0b0000000, // 28
			0b0000000, // 29
			0b0000000, // 30
			0b0000000, // 31
			0b0000000, // Space
			0b0000110, // !
			0b0100010, // "
			0b1100011, // #
			0b1101101, // $
			0b0101101, // %
			0b1110000, // &
			0b0000010, // '
			0b0111001, // (
			0b0001111, // )
			0b1011100, // *
			0b1110000, // +
			0b0001100, // ,
			0b1000000, // -
			0b0000100, // .
			0b1010010, // /
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
			0b0001001, // :
			0b0001101, // ;
			0b0011000, // <
			0b1001000, // =
			0b0000011, // >
			0b1010011, // ?
			0b1011100, // @
			0b1110111, // A
			0b1111100, // B
			0b0111001, // C
			0b1011110, // D
			0b1111001, // E
			0b1110001, // F
			0b0111101, // G
			0b1110110, // H
			0b0110000, // I
			0b0011110, // J
			0b1110101, // K
			0b0111000, // L
			0b0110111, // M
			0b1010100, // N
			0b0111111, // O
			0b1110011, // P
			0b1100111, // Q
			0b1010000, // R
			0b1101101, // S
			0b1111000, // T
			0b0111110, // U
			0b0011000, // V
			0b1111110, // W
			0b1110110, // X
			0b1101110, // Y
			0b1011011, // Z
			0b0111001, // [
			0b1100100, // Backslash
			0b0001111, // ]
			0b0100001, // ^
			0b0001000, // _
			0b0000010, // `
			0b1011111, // a
			0b1111100, // b
			0b1011000, // c
			0b1011110, // d
			0b1111011, // e
			0b1110001, // f
			0b1101111, // g
			0b1110100, // h
			0b0010000, // i
			0b0001100, // j
			0b1110101, // k
			0b0110000, // l
			0b0110111, // m
			0b1010100, // n
			0b1011100, // o
			0b1110011, // p
			0b1100111, // q
			0b1010000, // r
			0b1101101, // s
			0b1111000, // t
			0b0111110, // u
			0b0011000, // v
			0b1111110, // w
			0b1110110, // x
			0b1101110, // y
			0b1011011, // z
			0b0111001, // {
			0b0000110, // |
			0b0001111, // }
			0b1000000, // ~
			0b0000000, // DEL
	};
	
	static byte get7SegmentCode(char c) {
		if (c >= table.length)
			return 0;
		if (c < 0)
			return 0;
		return table[c];
	}
}
