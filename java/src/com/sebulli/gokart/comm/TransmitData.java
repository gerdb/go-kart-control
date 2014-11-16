/**
 *  Project     Go-Kart Control
 *  @author		Gerd Bartelt - www.sebulli.com
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

import java.util.ArrayList;
import java.util.List;

import com.sebulli.gokart.gui.LookUp7Segment;



public class TransmitData {
	
	private List<Integer> powerValues;
	private int flagValue = 0;
	private int watchdogTimeout = 0;
	private byte[] displayValues = new byte[3];
	
	public TransmitData(int panels) {
		powerValues = new ArrayList<Integer>();
		for (int i = 0; i <= panels; i++) {
			powerValues.add(0);
		}
	}
	
	public void setPowerValue(int i, int value) {
		powerValues.set(i, value);
	}
	
	public int getPowerValue(int i) {
		return powerValues.get(i);
	}

	public byte[] getDisplayValue() {
		return displayValues;
	}

	public void setDisplayValue(String displayValue) {
		String s = "   "  + displayValue;
		s = s.substring(s.length()-3);
		for (int i = 0; i< 3; i++)
			displayValues[i] = LookUp7Segment.get7SegmentCode((char)(s.getBytes()[i]));
	}

	public int getFlagValue() {
		return flagValue;
	}

	public void setFlagValue(int flagValue) {
		this.flagValue = flagValue;
	}

	public int getWatchdogTimeout() {
		return watchdogTimeout;
	}

	public void setWatchdogTimeout(int watchdogTimeout) {
		this.watchdogTimeout = watchdogTimeout;
	}

}
