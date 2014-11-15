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

import java.awt.SplashScreen;

import com.sebulli.gokart.comm.Communication;
import com.sebulli.gokart.gui.Gui;

/**
 * The projects main class
 *
 */
public class Main {

	private static Communication com;

	/**
	 * The main function
	 * 
	 * @param args
	 *            Command line arguments
	 */
	public static void main(String[] args) {

		// Some system information
		System.out.println("System.java.library.path: " + System.getProperty("java.library.path"));
		System.out.println("System.java.home: " + System.getProperty("java.home"));

		// Splas screen
		final SplashScreen splash = SplashScreen.getSplashScreen();
		if (splash != null) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			splash.close();
		}

		// Load some objects
		Translate.loadLocaleSettings();
		com = new Communication();
		com.open();
		// Generate the gui
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Gui(com);
			}
		});
	}

}
