/**
 *  Project     Go-Kart Control
 *  @file		Config.java
 *  @author		Gerd Bartelt - www.sebulli.com
 *  @brief		Handles configuration settings
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

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Config {

	private static Config _instance = null;

	private Properties props = null;

	private Config() {
		props = new Properties();
		try {
			FileInputStream fis = new FileInputStream(new File("settings.txt"));
			props.load(fis);
		} catch (Exception e) {
			// catch Configuration Exception right here
		}
	}

	public synchronized static Config getInstance() {
		if (_instance == null)
			_instance = new Config();
		return _instance;
	}

	// get property value by name
	public String getProperty(String key) {
		String value = "";
		if (props.containsKey(key))
			value = (String) props.get(key);
		else {
			Logger.getInstance().log("Key '" + key + "' not found.");
			return "";
		}
		return value.trim();
	}
	// get property value by name
	public String getPropertyIfExists(String key) {
		String value = "";
		if (props.containsKey(key))
			value = (String) props.get(key);
		else {
			return "";
		}
		return value.trim();
	}
	
	public boolean isSet(String key) {
		if (!props.containsKey(key))
			return false;
		
		String value = (String) props.get(key);
		return !value.trim().isEmpty();
		
	}

	public int getPropertyAsInt(String key) {
		String property = getProperty(key);
		if (!property.isEmpty()) {
			try {
				return Integer.parseInt(property);
			} catch (Exception e) {
				Logger.getInstance().log("Error parsing key '" + key + "' as integer.");
				return 0;
			}
		}
		else {
			return 0;
		}

	}

}
