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

package com.sebulli.gokart;

import java.util.Locale;

import javax.swing.JTextArea;

/**
 * Logs messages in the logger window
 *
 */
public class Logger {

	private static Logger _instance = null;
	private String text = "";
	private JTextArea textArea = null;

	// Maximum length of logger text
	private static final int MAX_TEXT = 10000;

	/**
	 * Constructor Start with some system information
	 * 
	 */
	private Logger() {
		log("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " "
				+ System.getProperty("os.version"));

		log("Java: " + System.getProperty("java.vendor") + " " + System.getProperty("java.version") + " "
				+ System.getProperty("java.home"));
		log("Language: " + Locale.getDefault());
	}

	/**
	 * Generate a singleton
	 * 
	 * @return a Reference to the class
	 */
	public synchronized static Logger getInstance() {
		if (_instance == null)
			_instance = new Logger();
		return _instance;
	}

	/**
	 * Add a new message to the logger window
	 * 
	 * @param line
	 *            The new message
	 */
	public void log(String line) {
		text += line + "\n";

		// limit to MAX_TEXT characters
		if (text.length() > MAX_TEXT) {
			text = text.substring(text.length() - MAX_TEXT);
			int nl = text.indexOf('\n');
			if ((nl > 0) && (nl < 50))
				text = text.substring(nl + 1);
		}
		refresh();
	}

	/**
	 * Sets a reference to the message window
	 * 
	 * @param textArea
	 *            The control component
	 */
	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
		refresh();
	}

	/**
	 * Update the logger window with the new text
	 */
	private void refresh() {
		if (textArea != null) {
			textArea.setText(text);
		}

	}

}
