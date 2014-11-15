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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.sebulli.gokart.Config;
import com.sebulli.gokart.Logger;
import com.sebulli.gokart.comm.Communication;

/**
 * 
 * Displays the main gui of the application
 *
 */
public class Gui implements ActionListener {

	private List<GokartPanel> gokartPanels = new ArrayList<GokartPanel>();

	// Control elements
	private JButton sendButton;
	private JRadioButton offButton;
	private JRadioButton redButton;
	private JRadioButton yellowButton;
	private JRadioButton blueButton;
	private JTextField numberControl;

	/**
	 * Constructor of the gui
	 * 
	 * @param com
	 *            Reference to the communication object
	 */
	public Gui(Communication com) {

		// Generate the main frame
		JFrame frame = new JFrame("Go-Kart Control V1.0.0");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Image panel with go-kart track
		ImagePanel panel = new ImagePanel("pics/background.png");
		panel.setLayout(null);

		// Generate the go-kart panels
		// The amount depends on the settings in the config file
		int amountGokartPanels = Config.getInstance().getPropertyAsInt("panels.amount");
		for (int i = 1; i <= amountGokartPanels; i++) {
			GokartPanel gokartPanel = new GokartPanel(Config.getInstance().getProperty("panels." + i + ".name"));
			gokartPanel.setLocation(Config.getInstance().getPropertyAsInt("panels." + i + ".x"), Config.getInstance()
					.getPropertyAsInt("panels." + i + ".y"));
			gokartPanels.add(gokartPanel);
			panel.add(gokartPanel);
		}

		// The log window
		JTextArea logWindow = new JTextArea(5, 20);
		// logWindow.setEditable(false);
		logWindow.setBackground(new Color(200, 195, 190));
		JScrollPane scrollPane = new JScrollPane(logWindow, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		Logger.getLogger().setTextArea(logWindow);

		// The control panel with buttons
		JPanel controlPanel = new JPanel();

		GroupLayout layout = new GroupLayout(controlPanel);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		// T: Button text
		sendButton = new JButton(_("Send"));
		sendButton.setFont(new Font("Sans", Font.PLAIN, 40));
		sendButton.addActionListener(this);

		numberControl = new JTextField("", 3);
		numberControl.setFont(new Font("Sans", Font.PLAIN, 60));
		numberControl.setHorizontalAlignment(JTextField.RIGHT);

		JPanel radioButtonPanel = new JPanel();
		offButton = new JRadioButton("-");
		// T: Text of radio buttons
		redButton = new JRadioButton(_("red"));
		// T: Text of radio buttons
		yellowButton = new JRadioButton(_("yellow"));
		// T: Text of radio buttons
		blueButton = new JRadioButton(_("blue"));

		offButton.setFont(new Font("Sans", Font.PLAIN, 12));
		redButton.setFont(new Font("Sans", Font.PLAIN, 12));
		yellowButton.setFont(new Font("Sans", Font.PLAIN, 12));
		blueButton.setFont(new Font("Sans", Font.PLAIN, 12));

		offButton.setSelected(true);
		offButton.setMnemonic(0);
		redButton.setMnemonic(1);
		yellowButton.setMnemonic(2);
		blueButton.setMnemonic(3);

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(offButton);
		group.add(redButton);
		group.add(yellowButton);
		group.add(blueButton);

		radioButtonPanel.add(offButton);
		radioButtonPanel.add(redButton);
		radioButtonPanel.add(yellowButton);
		radioButtonPanel.add(blueButton);

		radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel, BoxLayout.PAGE_AXIS));

		layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(numberControl)
				.addComponent(radioButtonPanel).addComponent(sendButton)

		);

		layout.setVerticalGroup(layout.createSequentialGroup().addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(numberControl)
						.addComponent(radioButtonPanel).addComponent(sendButton))

		);

		controlPanel.setLayout(layout);

		frame.getContentPane().add(panel, BorderLayout.PAGE_START);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.getContentPane().add(controlPanel, BorderLayout.LINE_END);

		frame.setAlwaysOnTop(true);
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Action handler of the gui
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		// Send button was clicked
		if (e.getSource() == sendButton) {
			offButton.setSelected(true);
			numberControl.setText("");
		}
	}

}
