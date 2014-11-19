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

package com.sebulli.gokart.gui;
import static com.sebulli.gokart.Translate._;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.sebulli.gokart.Config;

/**
 * 
 * A JPanel with all control elements of the go-kart panel
 *
 */
public class GokartPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// Control elements of the panel
	private IconDisplay[] seg7s;
	private IconDisplay flag;
	private IconDisplay batt;
	private IconDisplay signal;
	private JLabel nameLabel;
	
	private double signalPoor = 0;
	private double signalGood = 0;
	private double battEmpty = 0;
	private double battFull = 0;
	/**
	 * Constructor Generates a go-kart panel with all control elements
	 * 
	 * @param name
	 *            The name of the panel, displayed in the right corner
	 */
	public GokartPanel(String name) {

		// Signal thresholds
		signalPoor = Config.getInstance().getPropertyAsDouble("signal.poor");
		signalGood = Config.getInstance().getPropertyAsDouble("signal.good");
		
		// Battery thresholds
		battEmpty = Config.getInstance().getPropertyAsDouble("battery.empty");
		battFull = Config.getInstance().getPropertyAsDouble("battery.full");
		
		// Set panel size
		Dimension size = new Dimension(100, 62);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setBorder(BorderFactory.createLineBorder(Color.black));
		setBackground(Color.white);
		setLayout(null);

		// Generate the control elements
		flag = new IconDisplay("flags.png", 4, 3);
		batt = new IconDisplay("battery.png", 11, 1);
		signal = new IconDisplay("signal.png", 7, 1);
		seg7s = new IconDisplay[3];
		for (int i = 0; i < 3; i++) {
			seg7s[i] = new IconDisplay("7segment.png", 16, 8);
			seg7s[i].setLocation(i * 20 + 3, 2);
			add(seg7s[i]);
		}

		nameLabel = new JLabel(name);
		nameLabel.setFont(new Font("Sans", Font.PLAIN, 10));
		nameLabel.setSize(nameLabel.getPreferredSize());

		// Place the control elements on the panel
		flag.setLocation(72, 2);
		batt.setLocation(3, 40);
		signal.setLocation(47, 43);
		nameLabel.setLocation(95 - nameLabel.getSize().width, 47);

		// Add them to the panel
		add(flag);
		add(batt);
		add(signal);
		add(nameLabel);

		// Set default values
		setDisplayValue(0,0);
		setDisplayValue(1,0);
		setDisplayValue(2,0);
		setFlagValue(0);
		setBattValue(0);
		setSignalValue(0);
	}

	/**
	 * Setter for the 7 segment display Fills the leading segments with spaces
	 * 
	 * @param s
	 */
	public void setDisplayValue(int i, int code) {
		seg7s[i].setValue(code);
	}

	/**
	 * Setter for the flag value
	 * 
	 * @param value
	 *            The value to set (index of the icon set)
	 */
	public void setFlagValue(int value) {
		if (value == 1)
			flag.setValue(1);
		else if (value == 2)
			flag.setValue(2);
		else if (value == 4)
			flag.setValue(3);
		else 
			flag.setValue(0);
		
	}

	/**
	 * Setter for the battery value
	 * 
	 * @param value
	 *            The value to set (index of the icon set)
	 */
	public void setBattValue(double value) {
		batt.setToolTipText(String.format("%2.1f", value + 0.01) + "V");
		if (value < 5.0) {
			batt.setValue(0);
		} else if (value <= battEmpty) {
			batt.setValue(1);	
		} else {
			batt.setValue(1 + (int)(10*((double)value - battEmpty) / (battFull - battEmpty)));
		}
			
	}

	/**
	 * Setter for the signal value
	 * 
	 * @param value
	 *            The value to set (index of the icon set)
	 */
	public void setSignalValue(int value) {
		if (value  == 0) {
			// T: ToolTip text
			signal.setToolTipText(_("No signal"));
			signal.setValue(0);
		} else {
			signal.setToolTipText(value + "dBm");
			signal.setValue(1 + (int)(6*((double)value - signalPoor) / (signalGood - signalPoor)));
		}
	}
	
	/**
	 * Setter for serial number
	 * 
	 * @param value
	 *            The value to set (index of the icon set)
	 */
	public void setSerialNumber(String snr) {
		nameLabel.setToolTipText(snr);
	}

}
