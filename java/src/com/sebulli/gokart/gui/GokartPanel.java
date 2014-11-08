/**
 *  Project     Go-Kart Control
 *  @file		GokartPanel.java
 *  @author		Gerd Bartelt - www.sebulli.com
 *  @brief		A JPanel with all control elements of the go-kart panel
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GokartPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private String display;
	String name = "---";
	private IconDisplay[] seg7s = new IconDisplay[3];
	
	public GokartPanel(String name) {
		this.name = name;
		Dimension size = new Dimension(100, 62);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setBorder(BorderFactory.createLineBorder(Color.black));
		setBackground(Color.white);
		setLayout(null);


		IconDisplay flag = new IconDisplay( "flags.png", 4 , 1);
		IconDisplay batt = new IconDisplay( "battery.png", 11 , 1);
		IconDisplay signal = new IconDisplay( "signal.png", 7 , 1);
		
		JLabel nameLabel = new JLabel(name);
		nameLabel.setFont(new Font("Sans", Font.PLAIN, 10));
		nameLabel.setSize(nameLabel.getPreferredSize());
		
		for (int i = 0; i<3; i++) {
			seg7s[i] = new IconDisplay( "7segment.png", 16 , 8);
			seg7s[i].setLocation(i*20 + 3, 2);
			add(seg7s[i]);
		}
		
		flag.setLocation(72, 2);
		batt.setLocation(3, 40);
		signal.setLocation(47, 43);
		nameLabel.setLocation(95- nameLabel.getSize().width, 47);
		
		add(flag);
		add(batt);
		add(signal);
		add(nameLabel);
		setDisplayValue("012");
		flag.setValue(1);
		batt.setValue(5);
		signal.setValue(5);
	}
	public void setDisplayValue(String s) {
		
		// Get the last 3 characters with leading spaces
		display = "   " + s;
		display = display.substring(display.length()-3);
		
		for (int i = 0; i<3; i++) {
			if (display.length()>i)
				seg7s[i].setValue(LookUp7Segment.get7SegmentCode(display.charAt(i)));
			else
				seg7s[i].setValue(0);
		}
	}


}
