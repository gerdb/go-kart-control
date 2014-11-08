/**
 *  Project     Go-Kart Control
 *  @file		Main.java
 *  @author		Gerd Bartelt - www.sebulli.com
 *  @brief		The projects main class
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

public class Main {

	private static Communication com;

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.

		System.out.println("System.java.library.path: " + System.getProperty("java.library.path"));
		System.out.println("System.java.home: " + System.getProperty("java.home"));

		final SplashScreen splash = SplashScreen.getSplashScreen();
		if (splash != null) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			splash.close();
		}

		Translate.loadLocaleSettings();
		com = new Communication();

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Gui(com);
			}
		});
	}

}
