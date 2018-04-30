// Copyright (c) 2018 The Midi2Drum developers
// Distributed under the MIT software license, see the accompanying
// file COPYING or http://www.opensource.org/licenses/mit-license.php.

import java.io.File;
import java.io.IOException;

import javax.sound.midi.MidiUnavailableException;

import midi2drum.MidiToITR;

public class TestMidi2Drum {

	public static void main(String[] args) throws IOException, MidiUnavailableException {
		System.out.println("<<<<start of Creation>>>>");
		MidiToITR midiToITR;
		// "./resources/River Flows in you.mid"
		//TODO put midifile to github
		String MIDIFileName = "Guns_n_Roses_-_Sweet_Child_O_Mine.mid";
		String artisName = "Guns n Roses";
		String titleName = "Guns_n_Roses_-_Sweet_Child_O_Mine";
		String producerName = "Auto Generated midi2Drum";
		int level = 11;
		
		midiToITR = new MidiToITR("resources" + File.separator + "testdata" + File.separator + MIDIFileName );
		midiToITR.printInformationMidiFile();
		System.out.println("Detecting Drums Channel");
		System.out.println("Drum Channel is:" + midiToITR.getNumTrackDrums());
		if (midiToITR.getNumTrackDrums() >= 0) {
			
			String resourcesPath = new File("resources").getAbsolutePath();
			String outputPath = new File("output").getAbsolutePath();
			
			double volume = 0.7;
			midiToITR.convertTrackToITR(outputPath + File.separator + titleName + ".itr", artisName, titleName, titleName + ".wav",
					producerName, level, volume , "Guns_n_Roses.jpg", midiToITR.getNumTrackDrums(), 1);
			midiToITR.createSingleNoteMidi(resourcesPath + File.separator + "Guns_n_Roses_-_Sweet_Child_O_Mine.mid", 
					midiToITR.getNumTrackDrums(),titleName);
		
			midiToITR.convertMidiToWavFile( resourcesPath + File.separator + "Guns_n_Roses_-_Sweet_Child_O_Mine.mid",
					outputPath + File.separator + "BGM" + File.separator + titleName + ".wav");
			
		}

		// midiToITR.sequencerStart();
		midiToITR.close();

		System.out.println("<<<<end of Creation>>>>");
		// System.exit(0);
		;
	}

}
