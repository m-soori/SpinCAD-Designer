/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * rms_limiterControlPanel.java
 * Copyright (C) 2015 - Gary Worsham 
 * Based on ElmGen by Andrew Kilpatrick 
 * 
 *   This program is free software: you can redistribute it and/or modify 
 *   it under the terms of the GNU General Public License as published by 
 *   the Free Software Foundation, either version 3 of the License, or 
 *   (at your option) any later version. 
 * 
 *   This program is distributed in the hope that it will be useful, 
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 *   GNU General Public License for more details. 
 * 
 *   You should have received a copy of the GNU General Public License 
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *     
 */ 
package com.holycityaudio.SpinCAD.ControlPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ItemEvent;
import javax.swing.BoxLayout;
import javax.swing.JSlider;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.Box;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.Dimension;
import java.text.DecimalFormat;
import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.spinCADControlPanel;
import com.holycityaudio.SpinCAD.CADBlocks.rms_limiterCADBlock;

public class rms_limiterControlPanel extends spinCADControlPanel {
	private JFrame frame;

	private rms_limiterCADBlock gCB;
	// declare the controls
	JSlider inGainSlider;
	JLabel  inGainLabel;	
	JSlider filtSlider;
	JLabel  filtLabel;	

public rms_limiterControlPanel(rms_limiterCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("RMS_Limiter");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			
			inGainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.1 * 100.0),(int) (1.0 * 100.0), (int) (gCB.getinGain() * 100.0));
				inGainSlider.addChangeListener(new rms_limiterListener());
				inGainLabel = new JLabel();
				updateinGainLabel();
				
				Border inGainborder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel inGaininnerPanel = new JPanel();
					
				inGaininnerPanel.setLayout(new BoxLayout(inGaininnerPanel, BoxLayout.Y_AXIS));
				inGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				inGaininnerPanel.add(inGainLabel);
				inGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				inGaininnerPanel.add(inGainSlider);		
				inGaininnerPanel.setBorder(inGainborder);
			
				frame.add(inGaininnerPanel);
			
			filtSlider = gCB.LogFilterSlider(10,100,gCB.getfilt());
				filtSlider.addChangeListener(new rms_limiterListener());
				filtLabel = new JLabel();
				updatefiltLabel();
				
				Border filtborder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel filtinnerPanel = new JPanel();
					
				filtinnerPanel.setLayout(new BoxLayout(filtinnerPanel, BoxLayout.Y_AXIS));
				filtinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				filtinnerPanel.add(filtLabel);
				filtinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				filtinnerPanel.add(filtSlider);		
				filtinnerPanel.setBorder(filtborder);
			
				frame.add(filtinnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocation(gCB.getX() + 100, gCB.getY() + 100);
				frame.setAlwaysOnTop(true);
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class rms_limiterListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == inGainSlider) {
			gCB.setinGain((double) (inGainSlider.getValue()/100.0));
				updateinGainLabel();
			}
			if(ce.getSource() == filtSlider) {
			gCB.setfilt((double) gCB.freqToFilt(gCB.sliderToLogval((int)(filtSlider.getValue()), 10.0)));
				updatefiltLabel();
			}
			}
		}

		// add item listener 
		class rms_limiterItemListener implements java.awt.event.ItemListener { 
		public void stateChanged(ChangeEvent ce) {
			}
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
			}
		}
		
		// add action listener 
		class rms_limiterActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updateinGainLabel() {
		inGainLabel.setText("Input_Gain " + String.format("%4.2f", gCB.getinGain()));		
		}		
		private void updatefiltLabel() {
		//				kflLabel.setText("HF damping freq 1:" + String.format("%4.1f", gCB.filtToFreq(gCB.getkfl())) + " Hz");		
						filtLabel.setText("Filter " + String.format("%4.1f", gCB.filtToFreq(gCB.getfilt())) + " Hz");		
		}		
		
		class MyWindowListener implements WindowListener
		{
		@Override
			public void windowActivated(WindowEvent arg0) {
			}

		@Override
			public void windowClosed(WindowEvent arg0) {
			}

		@Override
			public void windowClosing(WindowEvent arg0) {
				gCB.clearCP();
			}

		@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
		}

		@Override
		public void windowIconified(WindowEvent arg0) {

		}

			@Override
			public void windowOpened(WindowEvent arg0) {
			}
		}
		
	}
