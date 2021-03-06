package com.holycityaudio.SpinCAD.test;

import javax.swing.SwingUtilities;

import com.holycityaudio.SpinCAD.SpinCADFrame;

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.simulator.SimulatorState;
import org.andrewkilpatrick.elmGen.simulator.SinLFO;

public class SpinCADTest {

	public SpinCADTest() {
	}
	
	// ------------------------------------------------------------
	/**
	 * 
	 * Launch the application.
	 */
	
	public static void main(String[] args) {
		
		
		final ElmProgram p = new ElmProgram("Test");
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try { 
					
					SpinCADFrame dspFrame = new SpinCADFrame();
					dspFrame.setVisible(true);
/*					
					// these were generated by GenTestCases() - old Java written CADBlocks
					new BitCrusherTest(dspFrame);
					new SingleDelayTest(dspFrame);
					new MultiTapTest(dspFrame);
					new HPF2PTest(dspFrame);
					new OneBandEQTest(dspFrame);
					new SixBandEQTest(dspFrame);
					new BiQuadTest(dspFrame);
					new ChorusTest(dspFrame);
					new ChorusPresetTest(dspFrame);
					new DelayTest(dspFrame);
					new LPF4PTest(dspFrame);
					new CubeGainTest(dspFrame);
					// these are auto-generated from spincad source files
					new InstructionTestTest(dspFrame);
// wave shaper menu
					new ToverXTest(dspFrame);
					new OctaveTest(dspFrame);
					new distTest(dspFrame);	
// dynamics menu
					new rms_lim_expTest(dspFrame);
					new rms_limiterTest(dspFrame);
					new soft_knee_limiterTest(dspFrame);
					new slow_gearTest(dspFrame);
// reverb menu
					new allpassTest(dspFrame);
					new reverbTest(dspFrame);
					new rom_rev2Test(dspFrame);

*/					


				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
	
	
	void LFO_Test() {
		int freq = 350;
		int amp = 4096;

		SimulatorState st = new SimulatorState();
		
		st.setRegVal(ElmProgram.SIN0_RATE, (freq & 0x1ff) << 14);
		st.setRegVal(ElmProgram.SIN0_RANGE, (amp & 0x7fff) << 8);
		
		SinLFO testLFO = new SinLFO(st, 0);
		for(int i = 0; i < 10000; i++) {
			testLFO.increment();
			System.out.println("Sin: " + testLFO.getSinValue());
		}
		
	}
	
}
