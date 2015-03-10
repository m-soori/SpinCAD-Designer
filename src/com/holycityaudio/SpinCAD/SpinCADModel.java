/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * SpinCADModel.java
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

package com.holycityaudio.SpinCAD;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.andrewkilpatrick.elmGen.ElmProgram;

import com.holycityaudio.SpinCAD.CADBlocks.FBInputCADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.FBOutputCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin.pinType;

public class SpinCADModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8461943977905967897L;
	String name = "SpinCADModel";
	ArrayList<SpinCADBlock> blockList = null;
	private static SpinCADBlock currentBlock = null;
	public static SpinFXBlock renderBlock = null;
	boolean changed = false;
	private int indexFB = 1;
	static int nBlocks = 0;

	public SpinCADModel() {
		newModel();
	}

	// newModel() clears out the current list
	public void newModel() {
		nBlocks = 0;
		blockList = new ArrayList<SpinCADBlock>();
		changed = false;
		indexFB = 1;
		setRenderBlock(new SpinFXBlock("Render Block"));
	}

	public int addBlock(SpinCADBlock pCB) {
		nBlocks++;
		blockList.add(pCB);
		pCB.setBlockNum(nBlocks);
		setCurrentBlock(pCB);
		changed = true;
		return 0;
	}

	public int modelSort() {
		Iterator<SpinCADBlock> itr = blockList.iterator();
		//		System.out.printf("Model sort...\n", name);
		SpinCADBlock block;
		int b = -1;
		int nSwaps = 0;

		while (itr.hasNext()) {
			block = itr.next();
			//			System.out.printf("Block %s [%d]\n", block.getName(), b);
			Iterator<SpinCADPin> itrPin = block.pinList.iterator();
			SpinCADPin currentPin;

			while (itrPin.hasNext()) {
				currentPin = itrPin.next();
				SpinCADBlock cB = currentPin.getBlockConnection();
				SpinCADPin cP = currentPin.getPinConnection();
				if (cB != null && cP != null) {
					// System.out.printf("Original [%s][%d] pin:[%s] ==> [%s] [%d] pin:[%s]\n",
					// block.getName(), block.getBlockNum(),
					// currentPin.getName(), cB.getName(), cB.getBlockNum(),
					// cP.getName());
					b = block.getBlockNum();
					if (b < cB.getBlockNum()) {
						int c = cB.getBlockNum();
						block.setBlockNum(c);
						cB.setBlockNum(b);
						nSwaps++;
						/*						System.out.printf("Swapped [%s][%d] pin:[%s] ==> [%s] [%d] pin:[%s]\n\n",
										block.getName(), block.getBlockNum(),
										currentPin.getName(), cB.getName(),
										cB.getBlockNum(), cP.getName());
						 */					}
				}
			}
		}
		return nSwaps;
	}

	public SpinCADBlock getBlock(int blockNum) {
		Iterator<SpinCADBlock> itr = blockList.iterator();
		// System.out.printf("get Block...\n", name);
		SpinCADBlock block;
		int b = -1;

		while (itr.hasNext()) {
			block = itr.next();
			b = block.getBlockNum();
			if (b == blockNum) {
				// System.out.printf("Name %s [%d]\n", block.getName(), b);
				return block;
			}
		}
		return null;
	}

	public int realign() {
		ArrayList<SpinCADBlock> sortedList = new ArrayList<SpinCADBlock>();
		//		System.out.printf("Realign...\n", name);
		SpinCADBlock block;
		int blockNumMin = 32768;
		SpinCADBlock blockMin = null;

		Iterator<SpinCADBlock> itr = blockList.iterator();
		while (itr.hasNext()) {

			Iterator<SpinCADBlock> itr2 = blockList.iterator();
			while (itr2.hasNext()) {
				block = itr2.next();
				if (block.getBlockNum() < blockNumMin) {
					blockMin = block;
					blockNumMin = block.getBlockNum();
				}
				// now blockMin is the block with the lowest number
			}
			sortedList.add(blockMin);
			blockList.remove(blockMin);

			// adjust iterator and blockNumMin after removing lowest numbered
			// block
			itr = blockList.iterator();
			blockNumMin = 32768;
		}

		blockList = sortedList;
		return 0;
	}

	/*
	 * presetIndexFB scans through the model immediately after file loading
	 * to find the top assigned IndexFB.  It sets indexFB to one more the the highest one in
	 * the model being loaded.
	 * There could be holes in the list of feedback loop indices due to deletion.
	 * It doesn't really matter as long as they are unique.
	 */

	public int presetIndexFB() {
		SpinCADBlock block;
		int index = 0;

		Iterator<SpinCADBlock> itr = blockList.iterator();
		while (itr.hasNext()) {
			block = itr.next();
			int i = block.getIndex();
			if(i > index) {
				index = i + 1;
			}
		}
		setIndexFB(index);
		return index;
	}

	public int sortAlignGen() {
		SpinCADBlock block = null;
		Iterator<SpinCADBlock> itr = blockList.iterator();
		while (itr.hasNext()) {
			block = itr.next();
			if(modelSort() == 0)
				break;
		}		
		realign();
		int i = generateCode();
		ElmProgram.checkCodeLen();
		return i;
	}

	public int generateCode() {
		setRenderBlock(new SpinFXBlock("Put file name here"));
		SpinCADBlock block = null;
		Iterator<SpinCADBlock> itr = blockList.iterator();
		int i = 0;

		// have to initialize FeedBack block registers to -1 or the next part doesn't work the second time!

		while (itr.hasNext()) {
			block = itr.next();
			if(block instanceof FBInputCADBlock)
			{
				((FBInputCADBlock) block).setRegister(-1);
			}
			else if(block instanceof FBOutputCADBlock)
			{
				((FBOutputCADBlock) block).setRegister(-1);
			}
		}

		itr = blockList.iterator();
		i = 0;

		while (itr.hasNext()) {
			block = itr.next();
			block.setBlockNum(i);
			if(block instanceof FBInputCADBlock)
			{
				// XXX under construction feedback block register resolution
				//search list to find matching FBOutputCADBlock
				// if it's not there, then allocateReg and set register to the returned value
				// if it is there, then it's already been allocated, so get the register value and
				// assign it here
				SpinCADBlock blockSearch = null;
				boolean found = false;
				Iterator<SpinCADBlock> itrFB = blockList.iterator();
				while (itrFB.hasNext()) {
					blockSearch = itrFB.next();
					if((blockSearch instanceof FBOutputCADBlock) && (blockSearch.getIndex() == block.getIndex()))
					{
						int i2 = ((FBOutputCADBlock) blockSearch).getRegister();
						if(i2 == -1) {
							i2 = getRenderBlock().allocateReg();
							((FBOutputCADBlock) blockSearch).setRegister(i2);
						}						
						((FBInputCADBlock) block).setRegister(i2);
						found = true;
					}
				}
				if(found == false) {
					int i1 = getRenderBlock().allocateReg();
					((FBInputCADBlock) block).setRegister(i1);
				}
			}
			else if(block instanceof FBOutputCADBlock)
			{
				//search list to find matching FBInputCADBlock
				// if it's not there, then allocateReg and set register to the returned value
				// if it is there, then it's already been allocated, so get the register value and
				// assign it here
				SpinCADBlock blockSearch = null;
				boolean found = false;
				Iterator<SpinCADBlock> itrFB = blockList.iterator();
				while (itrFB.hasNext()) {
					blockSearch = itrFB.next();
					if((blockSearch instanceof FBInputCADBlock) && (blockSearch.getIndex() == block.getIndex()))
					{
						int i3 = ((FBInputCADBlock) blockSearch).getRegister();
						if(i3 == -1) {
							i3 = getRenderBlock().allocateReg();
							((FBInputCADBlock) blockSearch).setRegister(i3);
						}
						((FBOutputCADBlock) block).setRegister(i3);
						found = true;
					}
				}
				if(found == false) {
					int i4 = getRenderBlock().allocateReg();
					((FBOutputCADBlock) block).setRegister(i4);
				}
			}
			i++;
			getRenderBlock();
			// TODO debug this, some problem with triple delay buffer assignments
			// no there is a problem here
			SpinFXBlock.setNumBlocks(block.getBlockNum());	// this is for keeping delay segments unique
			block.generateCode(getRenderBlock());
			// now blockMin is the block with the lowest number
		}
		System.out.println(getRenderBlock().getProgramListing(1));
		return getRenderBlock().getCodeLen() - getRenderBlock().getNumComments();
	}

	public static int countLFOReferences(String matchString) {
		String list = getRenderBlock().getProgramListing();
		int lastIndex = 0;
		int count =0;

		while(lastIndex != -1){

			lastIndex = list.indexOf(matchString,lastIndex);

			if( lastIndex != -1){
				count ++;
				lastIndex+=matchString.length();
			}
		}
		return count;
	}
	
	public static SpinFXBlock getRenderBlock() {
		return renderBlock;
	}

	public static void setRenderBlock(SpinFXBlock renderBlock) {
		SpinCADModel.renderBlock = renderBlock;
	}

	public static SpinCADBlock getCurrentBlock() {
		return currentBlock;
	}

	public static void setCurrentBlock(SpinCADBlock cB) {
		SpinCADModel.currentBlock = cB;
	}

	public void setChanged(boolean b) {
		changed = b;
	}
	public boolean getChanged() {
		return changed;
	}

	public void setIndexFB(int ijk) {
		indexFB = ijk;
	}

	public int getIndexFB() {
		return indexFB;
	}
}
