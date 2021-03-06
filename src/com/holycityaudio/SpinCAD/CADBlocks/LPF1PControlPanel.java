/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * LPF1PControlPanel.java
 * Copyright (C) 2013 - 2014 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2014.  Look for GSW in code.
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

package com.holycityaudio.SpinCAD.CADBlocks;

import java.awt.Point;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


class LPF1PControlPanel extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2288952347754535913L;

	JSlider freqSlider;
	JLabel freqLabel;

	private LPF1PCADBlock LPF;

	public LPF1PControlPanel(LPF1PCADBlock lpf1pcadBlock) {
		this.LPF = lpf1pcadBlock;
		createAndShowUI();
	}
	
	private void createAndShowUI() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setTitle("Low pass 1 pole");
				setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

				freqSlider = new JSlider(JSlider.HORIZONTAL, 80, 2500, 1000);
				freqSlider.addChangeListener(new LPF1PChangeListener());

				freqLabel = new JLabel();

				getContentPane().add(freqLabel);
				getContentPane().add(freqSlider);

				freqSlider.setValue((int)Math.round(LPF.getFreq()));
				updateFreqLabel();
				setVisible(true);
				setAlwaysOnTop(true);
				setLocation(new Point(LPF.getX() + 200, LPF.getY() + 150));
				pack();
				setResizable(false);
			}
		});		
	}

	class LPF1PChangeListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == freqSlider) {
				LPF.setFreq((double) freqSlider.getValue());
				updateFreqLabel();
			}
		}
	}
	
	private void updateFreqLabel() {
		freqLabel.setText("Frequency " + String.format("%2.2f", LPF.getFreq()));		
	}

}