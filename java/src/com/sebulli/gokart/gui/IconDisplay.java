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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * 
 * A control element that displays different states in icons
 *
 */
class IconDisplay extends JPanel {

	private static final long serialVersionUID = 1L;

	private Image img;
	private int cntX, cntY;
	private Dimension sizeBitmap;
	private Dimension sizeIcon;
	private int value = 0;

	/**
	 * Create an icon display
	 * 
	 * @param resname
	 *            Name of the image resource that contains the icon set
	 * @param cntX
	 *            Amount of columns in the icon set
	 * @param cntY
	 *            Amount of line in the icon set
	 */
	public IconDisplay(String resname, int cntX, int cntY) {
		this.cntX = cntX;
		this.cntY = cntY;

		// Get the icon set
		img = new ImageIcon(getClass().getResource("/resources/" + resname)).getImage();

		// Set the size
		sizeBitmap = new Dimension(img.getWidth(null), img.getHeight(null));
		sizeIcon = new Dimension(sizeBitmap.width / cntX, sizeBitmap.height / cntY);
		setPreferredSize(sizeIcon);
		setMinimumSize(sizeIcon);
		setMaximumSize(sizeIcon);
		setSize(sizeIcon);
		setLayout(null);

	}

	/**
	 * Set the value of the icon display Limit it to the min and max index
	 * 
	 * @param value
	 *            Index of the selected icon
	 */
	public void setValue(int value) {
		this.value = value;
		if (this.value < 0)
			this.value = 0;
		if (this.value >= cntX * cntY)
			this.value = cntX * cntY - 1;

		this.repaint();
	}

	/**
	 * Paint the selected icon of the icon set
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, -(value % cntX * sizeIcon.width), -(value / cntX * sizeIcon.height), null);
	}

}