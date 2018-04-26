// Copyright (c) 2018 The Midi2Drum developers
// Distributed under the MIT software license, see the accompanying
// file COPYING or http://www.opensource.org/licenses/mit-license.php.

package midi2drum;

//ITR XML <NormalNote ch="7" Measure="0" Position="10" TyoeIndex="0" Volume="1"/>
public class MyNote {

	int measure;
	int tickWithinMeasure;
	long midiTimeAbsolute;
	int midiChannel;
	int pitch;   //midiNote
	int velocity;

	public int getMeasure() {
		return measure;
	}

	public void setMeasure(int measure) {
		this.measure = measure;
	}

	public int getTickWithinMeasure() {
		return tickWithinMeasure;
	}

	public void setTickWithinMeasure(int tickWithinMeasure) {
		this.tickWithinMeasure = tickWithinMeasure;
	}

	public long getMidiTimeAbsolute() {
		return midiTimeAbsolute;
	}

	public void setMidiTimeAbsolute(int midiTimeAbsolute) {
		this.midiTimeAbsolute = midiTimeAbsolute;
	}

	public int getMidiChannel() {
		return midiChannel;
	}

	public void setMidiChannel(int midiChannel) {
		this.midiChannel = midiChannel;
	}

	public int getPitch() {
		return pitch;
	}

	public void setPitch(int pitch) {
		this.pitch = pitch;
	}

	public int getVelocity() {
		return velocity;
	}

	public void setVelocity(int velocity) {
		this.velocity = velocity;
	}
	
	/**
	 * @param measure
	 * @param tickWithinMeasure
	 * @param midiTimeAbsolute
	 * @param midiChannel
	 * @param pitch
	 * @param velocity 
	 */
	public MyNote(int measure, int tickWithinMeasure, long midiTimeAbsolute, int midiChannel, int pitch,
			int velocity) {
		this.measure = measure;
		this.tickWithinMeasure = tickWithinMeasure;
		this.midiTimeAbsolute = midiTimeAbsolute;
		this.midiChannel = midiChannel;
		this.pitch = pitch;
		this.velocity = velocity;
	}
}
