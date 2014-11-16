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
	
public class ReceiveData {
	
	private List<Double> BattValues;
	private List<Integer> RSSIValues;
	private List<ReceivePortBytes> PortBytes;
	
	
	public ReceiveData(int panels) {
		BattValues = new ArrayList<Double>();
		RSSIValues = new ArrayList<Integer>();
		PortBytes = new ArrayList<ReceivePortBytes>();
		
		for (int i = 0; i <= panels; i++) {
			BattValues.add(0.0);
			RSSIValues.add(0);
			PortBytes.add(new ReceivePortBytes());
		}

	}
	
	public void setBattValue(int i, double value) {
		BattValues.set(i, value);
	}
	
	public double getBattValue(int i) {
		return BattValues.get(i);
	}
	
	public void setRSSIValue(int i, int value) {
		RSSIValues.set(i, value);
	}
	
	public int getRSSIValue(int i) {
		return RSSIValues.get(i);
	}
	
	public void setPortByte(int i, int byteindex, byte value) {
		PortBytes.get(i).BYTES[byteindex] = value;
	}
	
	public void setPortByte(int i, int byteindex, int value) {
		setPortByte(i, byteindex, (byte) value);
	}
	
	public byte getPortByte(int i, int byteindex) {
		return PortBytes.get(i).BYTES[byteindex];
	}

}
