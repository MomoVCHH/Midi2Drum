// Copyright (c) 2018 The Midi2Drum developers
// Distributed under the MIT software license, see the accompanying
// file COPYING or http://www.opensource.org/licenses/mit-license.php.

package midi2drum;

import java.util.HashMap;
import java.util.Map;

public class GeneralMidiPercussionKeyMap {
	public static Map<Integer, String> gmPerMap = new HashMap<Integer, String>();
	static {
		gmPerMap.put(35, "Bass_Drum_2");
		gmPerMap.put(36, "Bass_Drum_1");
		gmPerMap.put(37, "Side_Stick");
		gmPerMap.put(38, "Snare_Drum_1");
		gmPerMap.put(39, "Hand_Clap");
		gmPerMap.put(40, "Snare_Drum_2");
		gmPerMap.put(41, "Low_Tom_2");
		gmPerMap.put(42, "Closed_Hi-hat");
		gmPerMap.put(43, "Low_Tom_1");
		gmPerMap.put(44, "Pedal_Hi-hat");
		gmPerMap.put(45, "Mid_Tom_2");
		gmPerMap.put(46, "Open_Hi-hat");
		gmPerMap.put(47, "Mid_Tom_1");
		gmPerMap.put(48, "High_Tom_2");
		gmPerMap.put(49, "Crash_Cymbal_1");
		gmPerMap.put(50, "High_Tom_1");
		gmPerMap.put(51, "Ride_Cymbal_1");
		gmPerMap.put(52, "Chinese_Cymbal");
		gmPerMap.put(53, "Ride_Bell");
		gmPerMap.put(54, "Tambourine");
		gmPerMap.put(55, "Splash_Cymbal");
		gmPerMap.put(56, "Cowbell");
		gmPerMap.put(57, "Crash_Cymbal_2");
		gmPerMap.put(58, "Vibra_Slap");
		gmPerMap.put(59, "Ride_Cymbal_2");
		gmPerMap.put(60, "High_Bongo");
		gmPerMap.put(61, "Low_Bongo");
		gmPerMap.put(62, "Mute_High_Conga");
		gmPerMap.put(63, "Open_High_Conga");
		gmPerMap.put(64, "Low_Conga");
		gmPerMap.put(65, "High_Timbale");
		gmPerMap.put(66, "Low_Timbale");
		gmPerMap.put(67, "High_Agogo");
		gmPerMap.put(68, "Low_Agogo");
		gmPerMap.put(69, "Cabasa");
		gmPerMap.put(70, "Maracas");
		gmPerMap.put(71, "Short_Whistle");
		gmPerMap.put(72, "Long_Whistle");
		gmPerMap.put(73, "Short_Guiro");
		gmPerMap.put(74, "Long_Guiro");
		gmPerMap.put(75, "Claves");
		gmPerMap.put(76, "High_Wood_Block");
		gmPerMap.put(77, "Low_Wood_Block");
		gmPerMap.put(78, "Mute_Cuica");
		gmPerMap.put(79, "Open_Cuica");
		gmPerMap.put(80, "Mute_Triangle");
		gmPerMap.put(81, "Open_Triangle");
	}

	public static String getNameOfDrumForMidiNote(int midiNote) {
		String s = gmPerMap.get(midiNote);
		if(s == null)
		{
			s = "MidiNote_" + Integer.toString(midiNote);
		}
		return s;
	}
}