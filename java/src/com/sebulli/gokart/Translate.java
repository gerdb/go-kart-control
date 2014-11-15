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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;

/**
 * Translate strings using gettext
 * 
 * @see http://www.gnu.org/software/gettext/
 * 
 */
public class Translate {

	private static Properties messages = null;

	private enum states {
		IDLE, MSGCTXT, MSGID, MSGSTR
	}

	/**
	 * Replace a string by the translated string. If no translation is
	 * available, return the original one.
	 * 
	 * @param s
	 *            String to translate
	 * @return The translated String
	 */
	public static String _(String s) {

		String sout;

		if (messages == null) {
			messages = new Properties();
			loadPoFile();
		}

		if (!messages.containsKey(s))
			return s;
		else {
			sout = messages.getProperty(s);
			if (sout.isEmpty())
				return s;
			else
				return sout;
		}
	}

	/**
	 * Replace a string by the translated string. If no translation is
	 * available, return the original one.
	 * 
	 * @param s
	 *            String to translate
	 * @param translate
	 *            TRUE, if the string should be translated
	 * @return The translated String
	 */
	public static String _(String s, boolean translate) {

		if (translate)
			return _(s);
		else
			return s;
	}

	/**
	 * Replace a string in a context by the translated string. If no translation
	 * is available, return the original one.
	 * 
	 * @param s
	 *            String to translate
	 * @param context
	 *            Context of the string
	 * @return The translated String
	 */
	public static String _(String s, String context) {

		// Context and string are added and separated by a vertical line
		String sWithContext = context + "|" + s;
		String sout;

		if (messages == null) {
			messages = new Properties();
			loadPoFile();
		}

		if (!messages.containsKey(sWithContext))
			return s;
		else {
			sout = messages.getProperty(sWithContext);
			if (sout.isEmpty())
				return s;
			else
				return sout;
		}
	}

	/**
	 * Replace a string by the translated string. If no translation is
	 * available, return the original one.
	 * 
	 * @param s
	 *            String to translate
	 * @param context
	 *            Context of the string
	 * @param translate
	 *            TRUE, if the string should be translated
	 * @return The translated String
	 */
	public static String _(String s, String context, boolean translate) {

		if (translate)
			return _(s, context);
		else
			return s;
	}

	/**
	 * Load a PO file from the resource and fill the properties
	 *
	 * @return url of the resource to load
	 */
	public static void loadPoFile(URL url) {
		states state = states.IDLE;
		String msgCtxt = "";
		String msgId = "";
		String msgStr = "";

		try {
			// Open the resource message po file.
			if (url == null)
				return;

			InputStream in = url.openStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF8"));
			String strLine;

			// Read file line by line
			while ((strLine = br.readLine()) != null) {

				// Search for lines with leading "msgctxt"
				if (strLine.startsWith("msgctxt")) {

					if (state != states.MSGCTXT)
						msgCtxt = "";

					// Set the state machine to MSGCTXT
					state = states.MSGCTXT;
					// Get the string
					strLine = strLine.substring(7).trim();
				}

				// Search for lines with leading "msgid"
				if (strLine.startsWith("msgid")) {

					if (state != states.MSGID)
						msgId = "";

					// Set the state machine to MSGID
					state = states.MSGID;
					// Get the string
					strLine = strLine.substring(5).trim();
				}

				// Search for lines with leading "msgstr"
				if (strLine.startsWith("msgstr")) {

					if (state != states.MSGSTR)
						msgStr = "";

					// Set the state machine to MSGSTR
					state = states.MSGSTR;
					// Get the string
					strLine = strLine.substring(6).trim();
				}

				// Find lines with no translation information
				if (!strLine.startsWith("\"")) {
					state = states.IDLE;
					msgCtxt = "";
					msgId = "";
					msgStr = "";
				} else {

					// Assemble the string and set the property
					if (state == states.MSGCTXT) {
						msgCtxt += format(strLine);
					}

					if (state == states.MSGID) {

						// Add the context to the message ID, separated by a "|"
						if (msgId.isEmpty()) {
							if (!msgCtxt.isEmpty()) {
								msgId = msgCtxt + "|";
								msgCtxt = "";
							}
						}
						msgId += format(strLine);
					}

					if (state == states.MSGSTR) {

						msgCtxt = "";
						msgStr += format(strLine);
						if (!msgId.isEmpty())
							messages.setProperty(msgId, msgStr);
					}
				}
			}
			// Close the input stream
			in.close();

		} catch (IOException e) {
			Logger.getLogger().log("Error loading message.po.");
		}
	}

	/**
	 * Load a PO file from the resource and fill the properties
	 */
	public static void loadPoFile() {

		File f = new File("lang/messages_" + Locale.getDefault() + ".po");
		if (!f.exists()) {
			f = new File("lang/messages_" + Locale.getDefault().getLanguage() + ".po");
			if (!f.exists()) {
				if (Locale.getDefault().getLanguage().equals("en") && !Config.getInstance().isSet("locale"))
					return;
			}
		}

		if (Config.getInstance().isSet("locale")) {
			f = new File("lang/messages_" + Locale.getDefault() + ".po");
		}

		if (f.exists()) {
			try {
				// Load it
				loadPoFile(f.toURI().toURL());
				return;
			} catch (Exception e) {
			}

		}
		Logger.getLogger().log("Language file " + f.getName() + " not found.");

	}

	/**
	 * Remove the trailing and leading quotes and unescape the string.
	 * 
	 * @param sin
	 *            The input string
	 * @return The formated string
	 */
	static String format(String sin) {
		sin = sin.trim();

		// Remove leading quotes
		if (sin.startsWith("\""))
			sin = sin.substring(1);

		// Remove trailing quotes
		if (sin.endsWith("\""))
			sin = sin.substring(0, sin.length() - 1);

		String sout = "";
		boolean escape = false;

		// Get character by character
		for (int i = 0; i < sin.length(); i++) {
			char c = sin.charAt(i);

			// Find the escape sequence
			if (c == '\\' && !escape)
				escape = true;
			else {
				if (escape) {

					// Replace the escape sequence
					if (c == '\'')
						sout += '\'';
					if (c == '\"')
						sout += '\"';
					if (c == '\\')
						sout += '\\';
					if (c == 'r')
						sout += '\r';
					if (c == 'n')
						sout += '\n';
					if (c == 'f')
						sout += '\f';
					if (c == 't')
						sout += '\t';
					if (c == 'b')
						sout += '\b';
					escape = false;
				} else {
					sout += c;
				}
			}
		}
		return sout;
	}

	/**
	 * Get the locale identifier from the configuration
	 */
	static void loadLocaleSettings() {
		String s[];
		String prop = Config.getInstance().getProperty("locale").trim();

		if (prop.isEmpty())
			return;

		s = prop.split("_");
		if (s.length == 1)
			Locale.setDefault(new Locale(s[0]));
		if (s.length == 2)
			Locale.setDefault(new Locale(s[0], s[1]));

	}

}
