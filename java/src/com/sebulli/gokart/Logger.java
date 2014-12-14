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
	private boolean newLine = true;
	
	// Maximum length of logger text
	private static final int MAX_TEXT = 10000;

	/**
	 * Constructor Start with some system information
	 * 
	 */
	private Logger() {
		log("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " "
				+ System.getProperty("os.version"), false);

		log("Java: " + System.getProperty("java.vendor") + " " + System.getProperty("java.version") + " "
				+ System.getProperty("java.home"), false);
		log("Language: " + Locale.getDefault(), false);
	}

	/**
	 * Generate a singleton
	 * 
	 * @return a Reference to the class
	 */
	public synchronized static Logger getLogger() {
		if (_instance == null)
			_instance = new Logger();
		return _instance;
	}
	public synchronized static Logger getLogger(Class<?> clazz) {
		return getLogger();
	}

	/**
	 * Add a new message to the logger window
	 * 
	 * @param line
	 *            The new message
	 */
	public void logText(String line, char startend, boolean asError) {

		if (((startend == ' ') || (startend == 's')) && (!newLine)) {
			line = "\n" + line;
		}
		if ((startend == ' ') || (startend == 'e')) {
			line = line + "\n";
			newLine = true;
		} else {
			newLine = false;
		}
		
		// Output also to the console
		if (asError)
			System.err.print(line);
		else
			System.out.print(line);
		
		text += line;

		// limit to MAX_TEXT characters
		if (text.length() > MAX_TEXT) {
			text = text.substring(text.length() - MAX_TEXT);
			int nl = text.indexOf('\n');
			if ((nl > 0) && (nl < 50))
				text = text.substring(nl + 1);
		}
		refresh();
	}
	
	public void logStart(String line) {
		logText(line, 's', false);
	}

	public void logEnd(String line) {
		logText(line, 'e', false);
	}
	
	public void logMiddle(String line) {
		logText(line, 'm', false);
	}
	
	public void log(String line , boolean asError) {
		logText(line, ' ', asError);
	}
	
	public void log(String line, Exception e) {
		log(line, true);
		e.printStackTrace();
	}
	
	public void log(String line) {
		log(line, false);
	}

	public void info(String line) {
		if (isInfoEnabled())
			log(line, false);
	}
	public void debug(String line) {
		if (isDebugEnabled())
			log(line, false);
	}
	public void debug(StringBuilder stringBuilder) {
		if (isDebugEnabled())
			log(stringBuilder.toString(), false);
	}
	public void warn(String line) {
		log(line, false);
	}
	public void warn(String line, Exception e) {
		log(line, e);
	}
	public void warn(String line, Throwable th) {
		log(line, false);
	}
	public void error(String line) {
		log(line, true);
	}
	public void error(String line, Exception e) {
		log(line, e);
	}
	public void error(Exception e) {
		if (e.getMessage() != null)
			log(e.getMessage());
		else if (!e.toString().isEmpty())
			log(e.toString());
		else {
			log ("Exception");
			e.printStackTrace();
		}
	}
	public boolean isInfoEnabled() {
		return false;
	}
	public boolean isDebugEnabled() {
		return false;
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
