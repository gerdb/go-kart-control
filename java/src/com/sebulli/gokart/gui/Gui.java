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
import javax.swing.Timer;

import com.sebulli.gokart.Config;
import com.sebulli.gokart.Logger;
import com.sebulli.gokart.comm.Communication;
import com.sebulli.gokart.comm.ReceiveData;
import com.sebulli.gokart.comm.TransmitData;

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
	private Communication com = null;
	private Timer timer;
	private TransmitData txdata;

	/**
	 * Constructor of the gui
	 * 
	 * @param com
	 *            Reference to the communication object
	 */
	public Gui(Communication com) {

		// Set a reference to the communication object
		this.com = com;

		// Generate the main frame
		JFrame frame = new JFrame("Go-Kart Control V1.0.0");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Image panel with go-kart track
		ImagePanel panel = new ImagePanel("pics/background.png");
		panel.setLayout(null);

		// Generate the go-kart panels
		// The amount depends on the settings in the config file
		int amountGokartPanels = Config.getInstance().getPropertyAsInt("panels.amount");

		txdata = new TransmitData(amountGokartPanels);
		
		for (int i = 1; i <= amountGokartPanels; i++) {
			GokartPanel gokartPanel = new GokartPanel(Config.getInstance().getProperty("panels." + i + ".name"));
			gokartPanel.setLocation(Config.getInstance().getPropertyAsInt("panels." + i + ".x"), Config.getInstance()
					.getPropertyAsInt("panels." + i + ".y"));
			gokartPanel.setSerialNumber(Config.getInstance().getProperty("panels." + i + ".serial"));
			gokartPanels.add(gokartPanel);
			txdata.setPowerValue(i, Config.getInstance().getPropertyAsInt("panels." + i + ".power"));
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
		yellowButton = new JRadioButton(_("yellow"));
		// T: Text of radio buttons
		blueButton = new JRadioButton(_("blue"));
		// T: Text of radio buttons
		redButton = new JRadioButton(_("red"));

		offButton.setFont(new Font("Sans", Font.PLAIN, 12));
		yellowButton.setFont(new Font("Sans", Font.PLAIN, 12));
		blueButton.setFont(new Font("Sans", Font.PLAIN, 12));
		redButton.setFont(new Font("Sans", Font.PLAIN, 12));

		offButton.setSelected(true);
		offButton.setMnemonic(0);
		yellowButton.setMnemonic(1);
		blueButton.setMnemonic(2);
		redButton.setMnemonic(3);

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(offButton);
		group.add(yellowButton);
		group.add(blueButton);
		group.add(redButton);

		radioButtonPanel.add(offButton);
		radioButtonPanel.add(yellowButton);
		radioButtonPanel.add(blueButton);
		radioButtonPanel.add(redButton);

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

		// Set main window on top 
		frame.setAlwaysOnTop(Config.getInstance().getPropertyAsInt("window.ontop") == 1);

		
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
		
		timer = new Timer(100, this);
		timer.setInitialDelay(100);
		timer.start(); 
	}

	/**
	 * Action handler of the gui
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// 100ms Timer
		if (e.getSource() == timer) {
			ReceiveData rxdata = com.exchangeData(txdata);
			for (int i=0; i< gokartPanels.size(); i++) {
				gokartPanels.get(i).setSignalValue(rxdata.getRSSIValue(i+1));
				gokartPanels.get(i).setBattValue(rxdata.getBattValue(i+1));
				gokartPanels.get(i).setFlagValue(rxdata.getPortByte(i+1, 3));
				for (int ii =0; ii<3; ii++)
					gokartPanels.get(i).setDisplayValue(ii,rxdata.getPortByte(i+1, ii));
			}
		}

		
		// Send button was clicked
		if (e.getSource() == sendButton) {
			
			// take the values to send them
			txdata.setDisplayValue(numberControl.getText());
			
			if (yellowButton.isSelected())
				txdata.setFlagValue(1);
			else if (blueButton.isSelected())
				txdata.setFlagValue(2);
			else if (redButton.isSelected())
				txdata.setFlagValue(4);
			else 
				txdata.setFlagValue(0);
			
			offButton.setSelected(true);
			numberControl.setText("");
			
			com.setNewValues();
		}
	}

}