// Copyright (c) 2018 The Midi2Drum developers
// Distributed under the MIT software license, see the accompanying
// file COPYING or http://www.opensource.org/licenses/mit-license.php.

package midi2drum;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class MyTrack {
	public List<MyNote> myNoteList = new ArrayList<MyNote>();
	public Set<Integer> midiNoteSet = new HashSet<Integer>();
	public SortedSet<Entry<Integer, Integer>> SortedSet;
	private TreeMap<Integer, Integer> midiNoteFrequencyHmap = new TreeMap<Integer, Integer>();
	public TreeMap<Integer, Integer> mappingDrumAssignment = new TreeMap<Integer, Integer>(); 
	
	private int maxVelocity = 0;
	private int minVelocity = 127;
	
	private String trackLabel;
	private String instrumentName;
	
	/**
	 * @return the intrumentName
	 */
	public String getInstrumentName() {
		return instrumentName;
	}

	/**
	 * @param intrumentName the intrumentName to set
	 */
	public void setInstrumentName(String instrumentName) {
		this.instrumentName = instrumentName;
	}

	/**
	 * @return the maxVelocity
	 */
	public int getMaxVelocity() {
		return maxVelocity;
	}

	/**
	 * @return the minVelocity
	 */
	public int getMinVelocity() {
		return minVelocity;
	}
	/**
	 * @return the trackLabel
	 */
	public String getTrackLabel() {
		return trackLabel;
	}

	/**
	 * @param trackLabel the trackLabel to set
	 */
	public void setTrackLabel(String trackLabel) {
		this.trackLabel = trackLabel;
	}

	public void addNote(int measure, int tickWithinMeasure, long midiTimeAbsolute,
			int midiChannel, int pitch, int velocity) {
		MyNote newNote = new MyNote(measure, tickWithinMeasure, midiTimeAbsolute, midiChannel, pitch, velocity);
		myNoteList.add(newNote);
		if(velocity < minVelocity) {
			minVelocity = velocity;
		}
		if(velocity > maxVelocity) {
			maxVelocity = velocity;
		}
		midiNoteSet.add(pitch);		
		Integer previousValue = midiNoteFrequencyHmap.get(pitch);
		midiNoteFrequencyHmap.put(pitch, previousValue == null ? 1 : previousValue + 1);
	}
	
	public int numberOfTones() {
		return myNoteList.size();
	}
	
	public void printOutMidiNoteSet()
	{
		System.out.println(midiNoteSet);
	}
	
	public void buildMidiNoteFrequencyHmap() {
		// TODO Auto-generated method stub
		SortedSet = entriesSortedByValues(midiNoteFrequencyHmap);
		System.out.println("sorted set midiNotes=Frequency:" + SortedSet);

		//assignment of drums
		int[] drum = {8,9,7,10,6,11,1,2,3,4,5}; //TODO createOwn
		@SuppressWarnings("rawtypes")
		Iterator it = SortedSet.iterator();
		int i = 0;
		int value;
	    while (it.hasNext()) {
	        @SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() +  " = " + pair.getValue() );
	        
	         //the most often drums should be channel 8 and 9
		     //then 7 and 10
			 //then 6 and 11
             //the other should be placed on a free channel from 6 to 11
	        
	        if(i < drum.length )
	        {
	        	value = drum[i];//8,9,7,10,6,11,...
				mappingDrumAssignment.put((Integer) pair.getKey(), value);
	        }
	        else 
	        {
	        	//value = i;
	        	//mappingDrumAssignment.put((Integer) pair.getKey(), value);
	        }	
	        ++i;
	    }
	    System.out.println("Mapping MidiNote=InGameChannel:" + mappingDrumAssignment);
	}
	
	/**
	 * @param map 
	 * @return
	 */
	static <K,V extends Comparable<? super V>>
	SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
	    SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
	        new Comparator<Map.Entry<K,V>>() {
	            @Override public int compare(Map.Entry<K,V> e2, Map.Entry<K,V> e1) {
	                int res = e1.getValue().compareTo(e2.getValue());
	                return res != 0 ? res : 1;
	            }
	        }
	    );
	    sortedEntries.addAll(map.entrySet());
	    return sortedEntries;
	}
}
