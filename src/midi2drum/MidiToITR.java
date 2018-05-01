// Copyright (c) 2018 The Midi2Drum developers
// Distributed under the MIT software license, see the accompanying
// file COPYING or http://www.opensource.org/licenses/mit-license.php.

package midi2drum;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class MidiToITR {

	// Instance Variables
	private Sequencer sequencer;
	private Sequence sequence;
	private File midiFile;
	private MidiFileFormat midiFileFormat;
	private Track[] tracks;
	private int totalMeasures;
	private int numTrackDrums = -1;
	private MyTrack[] myTrack; // midi suports 16 channels

	/**
	 * Ticks per second
	 */
	private double ticksPerSecond;
	private double tickSize;

	/**
	 * @return the numTrackDrums
	 */
	public int getNumTrackDrums() {
		if (numTrackDrums < 0) {
			System.err.println("Err: MidiTrack for Drums couldn't be found");
		}
		return numTrackDrums;
	}

	public void createDifferentMidiNotes(int numTrack) {
		System.out.println("MidiTrack " + numTrack + " has used the following set of different MidiNotes");
		myTrack[numTrack].printOutMidiNoteSet();
		myTrack[numTrack].buildMidiNoteFrequencyHmap();
	}

	public String getMidiFileInformation() {
		String s = "##MidiFileFormat##\n";
		Map<String, Object> properties = midiFileFormat.properties();

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			s += (entry.getKey() + "/" + entry.getValue() + "\n");
		}

		if (midiFileFormat != null) {
			s += "MidiFileFormat Type:" + midiFileFormat.getType();
			switch (midiFileFormat.getType()) {
			case 0:
				s += " - single Track Format";
				break;
			case 1:
				s += " - multi Track Format";
				break;
			case 2:
				s += " - unknown Track Format";
				break;
			}
			s += "\n";

		}
		return s;
	}

	public void createSingleNoteMidi(String midiFileInput, int numTrack, String titleName) throws IOException {
		for (Map.Entry<Integer, Integer> entry : myTrack[numTrack].mappingDrumAssignment.entrySet())
			try {
				System.out.println("Midifile creation for" + entry.getKey());
				createSingleDrumWav(entry.getKey(), titleName);
			} catch (MidiUnavailableException | InvalidMidiDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	private void createSingleDrumWav(int midiNote, String titleName)
			throws MidiUnavailableException, InvalidMidiDataException, IOException {
		System.out.println(sequence.getTracks().length);
		Sequence sequence2 = MidiSystem.getSequence(midiFile);
		// delete all tracks
		for (int i = sequence2.getTracks().length - 1; i >= 0; i--) {
			sequence2.deleteTrack(sequence2.getTracks()[i]);
		}

		// now sequenc2 is empty
		sequence2.createTrack();
		System.out.println(sequence.getTracks().length);
		System.out.println(sequence2.getTracks().length);
		System.out.println("NumTrackDrums:" + numTrackDrums + "-" + sequence2.getTracks()[0].size());

		// copy first single note
		MidiEvent e_start = null;
		;
		MidiEvent e_end = null;
		;
		for (int i = 0; i < sequence.getTracks()[numTrackDrums].size(); i++) {
			if (sequence.getTracks()[numTrackDrums].get(i).getMessage() instanceof ShortMessage) {
				ShortMessage s = (ShortMessage) (sequence.getTracks()[numTrackDrums].get(i).getMessage());
				if (s.getChannel() != 9) {

				} else {
					if (s.getCommand() == ShortMessage.NOTE_ON && s.getData1() == midiNote) {
						e_start = new MidiEvent(new ShortMessage(s.getStatus(), s.getData1(), 127), 1);

					}
					// midi_off OR midi_on with velocity 0 are two possibles to make a node_off in
					// MIDI Standard
					if ((s.getCommand() == ShortMessage.NOTE_OFF
							|| (s.getCommand() == ShortMessage.NOTE_ON && s.getData2() == 0))
							&& s.getData1() == midiNote) {
						e_end = new MidiEvent(new ShortMessage(s.getStatus(), s.getData1(), 0), 1);
						break;
					}
					if (s.getCommand() != ShortMessage.NOTE_OFF || s.getCommand() != ShortMessage.NOTE_OFF) {
						// sequence2.getTracks()[0].add(sequence.getTracks()[numTrackDrums].get(i));
					}
				}
			}
		}
		e_start.setTick(1);
		sequence2.getTracks()[0].add(e_start);
		e_end.setTick(sequence.getResolution() * 4);
		sequence2.getTracks()[0].add(e_end);

		System.out.println("NumTrackDrums:" + numTrackDrums + "-" + sequence2.getTracks()[0].size());
		System.out.println("NumTrackDrums:" + numTrackDrums + "-" + sequence.getTracks()[numTrackDrums].size());
		System.out.println("MidiNote:" + midiNote);
		File tempMidFile = File.createTempFile(GeneralMidiPercussionKeyMap.getNameOfDrumForMidiNote(midiNote), ".mid");
		try {
			MidiSystem.write(sequence2, 0, tempMidFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("createMidiFile done");
		// now convert it to wav format
		String outputWavFile = new File("Editor\\Note Sound\\CustomNoteSound\\" + titleName + File.separator
				+ GeneralMidiPercussionKeyMap.getNameOfDrumForMidiNote(midiNote)).getAbsolutePath() + ".wav";
		convertMidiToWavFile(tempMidFile.getAbsolutePath().toString(), outputWavFile);
		System.out.println(tempMidFile.getAbsolutePath());
		tempMidFile.delete();
	}

	/**
	 * @param numTrackDrums
	 *            the numTrackDrums to set
	 */
	public void setNumTrackDrums(int numTrackDrums) {
		this.numTrackDrums = numTrackDrums;
	}

	public double getTicksPerSecond() {
		return ticksPerSecond;
	}

	/**
	 * @param midiFileInput
	 *            Input the whole path a midiFile e.g. "./resources/River Flows in
	 *            you.mid"
	 * @throws MidiUnavailableException
	 * @throws IOException
	 * @throws InvalidMidiDataException
	 */
	public MidiToITR(String midiFileInput) throws MidiUnavailableException {
		// Get default sequencer.
		sequencer = MidiSystem.getSequencer();
		if (sequencer == null) {
			System.err.println("Err:null reference MidiSystem.getSequencer()");
		} else {
			// Acquire resources and make operational.
			sequencer.open();
		}
		parseMidi(midiFileInput);
	}

	public MidiToITR(File midiFile) throws MidiUnavailableException, InvalidMidiDataException, IOException {
		// Get default sequencer.
		sequencer = MidiSystem.getSequencer();
		if (sequencer == null) {
			System.err.println("Err:null reference MidiSystem.getSequencer()");
		} else {
			// Acquire resources and make operational.
			sequencer.open();
		}
		this.midiFile = midiFile;
		parseMidi(midiFile);
	}

	public void close() {
		sequencer.close();
	}

	private void parseMidi(String midiFileInput) {
		midiFile = new File(midiFileInput);
		try {
			sequence = MidiSystem.getSequence(midiFile);
			midiFileFormat = MidiSystem.getMidiFileFormat(midiFile);
			sequencer.setSequence(sequence);
		} catch (InvalidMidiDataException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initInstanceVariables();
		goOverAllMidiTracks();
	}

	public void parseMidi(File midiFile) throws InvalidMidiDataException, IOException {
		sequence = MidiSystem.getSequence(midiFile);
		midiFileFormat = MidiSystem.getMidiFileFormat(midiFile);
		sequencer.setSequence(sequence);
		initInstanceVariables();
		goOverAllMidiTracks();
	}

	public void loadNewMidiFile(String midiFileInput) throws InvalidMidiDataException, IOException {
		parseMidi(midiFileInput);
	}

	private void initInstanceVariables() {
		numTrackDrums = -1;
		ticksPerSecond = sequence.getResolution() * (sequencer.getTempoInBPM() / 60.0);
		tickSize = 1.0 / ticksPerSecond;
		tracks = sequence.getTracks();
		myTrack = new MyTrack[tracks.length];
		setMetaInformationFromTracks();
		// System.out.println("tickSize:" + tickSize);
		// System.out.println("ticksPerSecond:" + ticksPerSecond);
	}

	private void goOverAllMidiTracks() {
		for (int i = 0; i < tracks.length; ++i) {
			Track track = sequence.getTracks()[i];
			System.out.println("Start Track " + i);
			System.out.println("Track num events:" + track.size());
			System.out.println("Track Ticks: " + track.ticks());
			System.out.println("");
			int j = 0;
			while (track.size() > 0 && j < track.size()) {

				// javax.soun.midi.ShortMessage MidiMessage with 2 Data Bytes
				if (track.get(j).getMessage() instanceof ShortMessage) {
					ShortMessage s = (ShortMessage) (track.get(j).getMessage());
					// find note on events
					if (s.getCommand() == ShortMessage.NOTE_ON) {
						// System.out.println(s.getLength() + " " + s.getChannel() +" " + s.getData1() +
						// " " + s.getData2());
						// measure
						if (s.getData2() > 0) {
							if (numTrackDrums == -1 && s.getChannel() == 9) {
								// 9 (the 10th) Midi Channel is always for Drums
								numTrackDrums = i;
							}

							int l_measure = (int) (1 + (track.get(j).getTick() / (4 * sequence.getResolution())));
							int l_tickInMeasure = (int) (1
									+ (track.get(j).getTick() % (4.0 * sequence.getResolution())));
							long l_midiTimeAbsolute = track.get(j).getTick();

							// calculate totalMeasure
							if (totalMeasures < l_measure) {
								totalMeasures = l_measure;
							}
							// we are only interested in Nodes of Channel 9 (DrumNodes)
							if (s.getChannel() == 9) {
								myTrack[i].addNote(l_measure, l_tickInMeasure, l_midiTimeAbsolute, s.getChannel(),
										s.getData1(), s.getData2());
								// System.out.println("add_drum_note channel 10");
							}

							// System.out.println("NoteOn: counter " + myTrack[i].numberOfTones());
							// System.out.println("track " + i + " / measure " + l_measure);
							// System.out.println("NoteOn: Tick_inMeasure:" + l_tickInMeasure);
							// System.out.println("NoteOn: Midi_Time_absolute " + track.get(j).getTick());
							// System.out.println("NoteOn: Midi Channel:" + s.getChannel());
							// System.out.println("NoteOn: pitch " + s.getData1());
							// System.out.println("NoteOn: velocity " + s.getData2() + "\n");

						}
					}
				}
				// metaMessage track name
				if (track.get(j).getMessage() instanceof MetaMessage) {
					MetaMessage metaMsg = (MetaMessage) (track.get(j).getMessage());

					byte[] abData = metaMsg.getData();
					switch (metaMsg.getType()) {
					case 1:
						String strText = new String(abData);
						System.out.println("MetaMessage - Text Event: " + strText);
						break;
					case 3:
						String strTrackName = new String(abData);
						System.out.println("MetaMessage - Sequence/Track Name: " + strTrackName);
						myTrack[i].setTrackLabel(strTrackName);
						/*
						 * if (strTrackName.contains("drums") || strTrackName.contains("Drums")) {
						 * numTrackDrums = i; }
						 */
						break;
					case 4:
						String strInstrumentName = new String(abData);
						System.out.println("MetaMessage - Instrument Name: " + strInstrumentName);
						myTrack[i].setInstrumentName(strInstrumentName);
						break;
					}
				}

				// note off not important in ITR Game
				if (track.get(j).getMessage() instanceof ShortMessage) {
					ShortMessage s = (ShortMessage) (track.get(j).getMessage());
					// find note on events
					if (s.getCommand() == ShortMessage.NOTE_OFF) {
						// System.out.println(s.getLength() + " " + s.getChannel() +" " + s.getData1() +
						// " " + s.getData2());
						// measure
						/*
						 * note_off not needed System.out.println("measure " + ( 1 +
						 * (track.get(j).getTick() / (4 * sequence.getResolution()) ) ) );
						 * System.out.println("NoteOFF: Midi_Time_absolute " + track.get(j).getTick());
						 * System.out.println("NoteOFF: Midi Channel:" + s.getChannel());
						 * System.out.println("NoteOFF: pitch " + s.getData1());
						 * System.out.println("NoteOFF: velocity " + s.getData2() + "\n");
						 */
					}
				}
				++j;
			}

			System.out.println("Track - total measures:" + totalMeasures);
			if (myTrack[i] != null) {
				System.out.println("Track - maxVelocity:" + myTrack[i].getMaxVelocity());
				System.out.println("Track - minVelocity:" + myTrack[i].getMinVelocity());
				System.out.print("Track - setOfInvolvdMidiNotes: ");
				myTrack[i].printOutMidiNoteSet();
			}
			System.out.println("End Track " + i);
		}
	}

	public void setMetaInformationFromTracks() {
		for (int i = 0; i < tracks.length; ++i) {
			Track track = sequence.getTracks()[i];
			myTrack[i] = new MyTrack();
			int j = 0;
			System.out.println("Track " + i);
			while (track.size() > 0 && j < track.size()) {
				if (track.get(j).getMessage() instanceof MetaMessage) {
					MetaMessage metaMsg = (MetaMessage) (track.get(j).getMessage());
					// metaMessage track name
					byte[] abData = metaMsg.getData();
					switch (metaMsg.getType()) {
					case 1:
						String strText = new String(abData);
						System.out.println("MetaMessage - Text Event: " + strText);
						break;
					case 3:
						String strTrackName = new String(abData);
						System.out.println("MetaMessage - Sequence/Track Name: " + strTrackName);
						myTrack[i].setTrackLabel(strTrackName);
						// not needed anymore (it's now done via midichannels)
						/*
						 * if (strTrackName.contains("drums") || strTrackName.contains("Drums")) {
						 * numTrackDrums = i; }
						 */
						break;
					case 4:
						String strInstrumentName = new String(abData);
						System.out.println("MetaMessage - Instrument Name: " + strInstrumentName);
						myTrack[i].setInstrumentName(strInstrumentName);
						break;
					}
				}
				j++;
			}
		}
	}

	public void printInformationMidiFile() {
		System.out.println("\n--general Midi File Information--");
		System.out.println(midiFile.getName() + " has length of " + midiFile.length() + " bytes");
		System.out.println(midiFile.getName() + ":");
		System.out.println("# Tracks:" + sequence.getTracks().length);
		System.out.println("length of Sequence:" + sequence.getMicrosecondLength() / 1000000.0 + " seconds");
		System.out.println("Division Type:" + sequence.getDivisionType());
		System.out.println("Tempo in MPQ" + sequencer.getTempoInMPQ());
		System.out.println("Tempo in BPM" + sequencer.getTempoInBPM());
		System.out.println("Resolution (Ticks of QuarterNote):" + sequence.getResolution());
		System.out.println("Resolution (Ticks of WholeNote):" + (4 * sequence.getResolution()));
		System.out.println("TickLength (Overall Ticks):" + sequence.getTickLength());
		System.out.println("tickSize:" + tickSize);
		System.out.println("ticksPerSecond:" + ticksPerSecond);
		System.out.println("Track - total measures:" + totalMeasures);
		System.out.println("");
	}

	public String getInformationMidiFile() {
		String s = "";
		s = s + "\n##general Midi File Information##\n";
		s = s + (midiFile.getName() + " has length of " + midiFile.length() + " bytes\n");
		s = s + (midiFile.getName() + ":\n");
		s = s + ("# Tracks:" + sequence.getTracks().length + "\n");
		s = s + ("length of Sequence:" + sequence.getMicrosecondLength() / 1000000.0 + " seconds" + "\n");
		s = s + ("Division Type:" + sequence.getDivisionType() + "\n");
		s = s + ("Tempo in MPQ" + sequencer.getTempoInMPQ() + "\n");
		s = s + ("Tempo in BPM" + sequencer.getTempoInBPM() + "\n");
		s = s + ("Resolution (Ticks of QuarterNote):" + sequence.getResolution() + "\n");
		s = s + ("Resolution (Ticks of WholeNote):" + (4 * sequence.getResolution()) + "\n");
		s = s + ("TickLength (Overall Ticks):" + sequence.getTickLength() + "\n");
		s = s + ("tickSize:" + tickSize + "\n" + "\n");
		s = s + ("ticksPerSecond:" + ticksPerSecond + "\n");
		s = s + ("Track - total measures:" + totalMeasures + "\n");
		return s;
	}

	/**
	 * @param itrFilePathName
	 * @param artistName
	 * @param titleName
	 * @param producerName
	 * @param levelNumber
	 * @param pathTitleImage
	 * @param tracknum
	 * @param offsetUntilStart
	 *            waiting time from Start to begin of the song in measures
	 */
	public void convertTrackToITR(String itrFilePathName, String artistName, String titleName, String tileWav,
			String producerName, int levelNumber, double volume, File titleImage, int tracknum, int offsetUntilStart) {

		createDifferentMidiNotes(getNumTrackDrums());
		File toCreate = new File("Editor" + File.separator + "Note Sound" + File.separator + "CustomNoteSound"
				+ File.separator + titleName);

		toCreate.getParentFile().getParentFile().getParentFile().mkdir();
		toCreate.getParentFile().getParentFile().mkdir();
		toCreate.getParentFile().mkdir();
		toCreate.mkdir();

		// copy titleImage to Editor\Image
		File destination = new File("Editor" + File.separator + "Image" + File.separator + titleImage.getName());
		destination.getParentFile().mkdir();

		try {
			Files.copy(titleImage.toPath(), (destination).toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		MyTrack myTrack1 = myTrack[tracknum];
		try {
			StringWriter stringWriter = new StringWriter();
			XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);

			xMLStreamWriter.writeStartDocument("utf-8", "1.0");
			xMLStreamWriter.writeCharacters("\r\n");
			xMLStreamWriter.writeStartElement("RhythmData");
			xMLStreamWriter.writeCharacters("\r\n  ");

			xMLStreamWriter.writeStartElement("SOUNDPATH");
			xMLStreamWriter.writeCharacters(tileWav);
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeCharacters("\r\n  ");

			xMLStreamWriter.writeStartElement("ARTIST");
			xMLStreamWriter.writeCharacters(artistName);
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeCharacters("\r\n  ");

			xMLStreamWriter.writeStartElement("TITLE");
			xMLStreamWriter.writeCharacters(titleName);
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeCharacters("\r\n  ");

			xMLStreamWriter.writeStartElement("EDITOR");
			xMLStreamWriter.writeCharacters(producerName);
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeCharacters("\r\n  ");

			xMLStreamWriter.writeStartElement("BPM");
			xMLStreamWriter.writeCharacters(Float.toString(sequencer.getTempoInBPM()));
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeCharacters("\r\n  ");

			xMLStreamWriter.writeStartElement("NUM_MEASURE");
			xMLStreamWriter.writeCharacters(Integer.toString(totalMeasures + offsetUntilStart)); // int offsetUntilStart
																									// waiting time
																									// until song begins
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeCharacters("\r\n  ");

			xMLStreamWriter.writeStartElement("LEVEL");
			xMLStreamWriter.writeCharacters(Integer.toString(levelNumber));
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeCharacters("\r\n  ");

			xMLStreamWriter.writeStartElement("TITLEIMAGE");
			xMLStreamWriter.writeCharacters(titleImage.getName());
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeCharacters("\r\n  ");

			xMLStreamWriter.writeStartElement("Type");
			xMLStreamWriter.writeCharacters("\r\n");
			Iterator<Integer> iter = myTrack1.midiNoteSet.iterator();

			int i = 0;
			while (iter.hasNext()) {

				// <TypeNode TypeIndex="0" ColorIndex="9" Type="./Note Sound/Sound
				// Pack/Rock/Kick.wav" />
				int midiNote = iter.next();
				xMLStreamWriter.writeCharacters("    "); // format characters
				xMLStreamWriter.writeEmptyElement("TypeNode");
				xMLStreamWriter.writeAttribute("TypeIndex", Integer.toString(midiNote));
				xMLStreamWriter.writeAttribute("ColorIndex", Integer.toString(i)); // color in GameEditor
				xMLStreamWriter.writeAttribute("Type", "./Note Sound/CustomNoteSound/" + titleName + "/"
						+ GeneralMidiPercussionKeyMap.getNameOfDrumForMidiNote(midiNote) + ".wav");
				xMLStreamWriter.writeCharacters("\r\n");
				++i;
			}
			xMLStreamWriter.writeCharacters("  ");
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeCharacters("\r\n");

			xMLStreamWriter.writeCharacters("  ");
			xMLStreamWriter.writeStartElement("OBJS");
			xMLStreamWriter.writeCharacters("\r\n");

			// write the BMGNote
			// e.g.<BGMNote ch="0" Measure="1" Position="0" BgmPath="Dancer.wav" Volume="1"
			// />
			xMLStreamWriter.writeCharacters("    ");
			xMLStreamWriter.writeEmptyElement("BGMNote");
			xMLStreamWriter.writeAttribute("ch", "0");
			xMLStreamWriter.writeAttribute("Measure", Integer.toString(offsetUntilStart));
			xMLStreamWriter.writeAttribute("Position", "0");
			xMLStreamWriter.writeAttribute("BgmPath", tileWav);
			xMLStreamWriter.writeAttribute("Volume", Double.toString(volume));
			xMLStreamWriter.writeCharacters("\r\n");

			float posQuote = (192.0f / (4 * sequence.getResolution()));

			for (i = 0; i < myTrack1.myNoteList.size(); i++) {
				// <NormalNote ch="7" Measure="0" Position="0" TypeIndex="0" Volume="1" />

				int drum;
				// select the channel
				if (myTrack1.mappingDrumAssignment.get(myTrack1.myNoteList.get(i).getPitch()) != null) {
					drum = myTrack1.mappingDrumAssignment.get(myTrack1.myNoteList.get(i).getPitch());
					xMLStreamWriter.writeCharacters("    ");
					xMLStreamWriter.writeEmptyElement("NormalNote");
					xMLStreamWriter.writeAttribute("ch", Integer.toString(drum));
				} else {
					System.out.println("TODO: lookup for a free place for this drum");
					continue;
				}

				xMLStreamWriter.writeAttribute("Measure", Integer.toString(myTrack1.myNoteList.get(i).measure)); // +
																													// offsetUntilStart));
				// xMLStreamWriter.writeAttribute("Position",
				// Integer.toString((int)(myTrack1.myNoteList.get(i).tickWithinMeasure *
				// posQuote)/3*3));
				xMLStreamWriter.writeAttribute("Position",
						Integer.toString((int) (myTrack1.myNoteList.get(i).tickWithinMeasure * posQuote)));
				xMLStreamWriter.writeAttribute("TypeIndex", Integer.toString(myTrack1.myNoteList.get(i).getPitch()));
				// xMLStreamWriter.writeAttribute("Volume",
				// Float.toString((float)myTrack1.myNoteList.get(i).velocity /
				// myTrack1.getMaxVelocity()));
				xMLStreamWriter.writeAttribute("Volume",
						// Float.toString((float) myTrack1.myNoteList.get(i).velocity
						// myTrack1.getMaxVelocity()));
						"1");
				xMLStreamWriter.writeCharacters("\r\n");
			}
			xMLStreamWriter.writeCharacters("  ");
			xMLStreamWriter.writeEndElement();

			xMLStreamWriter.writeCharacters("\r\n");
			xMLStreamWriter.writeCharacters("  ");
			xMLStreamWriter.writeStartElement("EditorPath");
			xMLStreamWriter.writeCharacters("\r\n");
			iter = myTrack1.midiNoteSet.iterator();

			while (iter.hasNext()) {
				// <EditorPath>
				// <DataGridViewNode Index="0" Type="./Note Sound/Sound Pack/Rock/Hihat_1.wav"
				// LabelType="Rock/Hihat_1.wav" Volumn="1" ColorIndex="12" />
				int midiNote = iter.next();
				xMLStreamWriter.writeCharacters("    "); // format characters
				xMLStreamWriter.writeEmptyElement("DataGridViewNode");
				xMLStreamWriter.writeAttribute("Index", Integer.toString(midiNote));
				xMLStreamWriter.writeAttribute("Type", "./Note Sound/CustomNoteSound/" + titleName + "/"
						+ GeneralMidiPercussionKeyMap.getNameOfDrumForMidiNote(midiNote) + ".wav");
				xMLStreamWriter.writeAttribute("LabelType", "./Note Sound/CustomNoteSound/" + titleName + "/"
						+ GeneralMidiPercussionKeyMap.getNameOfDrumForMidiNote(midiNote) + ".wav");
				xMLStreamWriter.writeAttribute("Volumn", "1");
				xMLStreamWriter.writeAttribute("ColorIndex", "0");
				xMLStreamWriter.writeCharacters("\r\n");
			}

			xMLStreamWriter.writeCharacters("  ");
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeCharacters("\r\n");
			xMLStreamWriter.writeEndDocument();
			xMLStreamWriter.flush();
			xMLStreamWriter.close();

			String xmlString = stringWriter.getBuffer().toString();

			stringWriter.close();

			System.out.println(xmlString);

			File file = new File(itrFilePathName);
			file.getParentFile().mkdir();
			file.getParentFile().getParentFile().mkdir();
			java.io.FileWriter fw = new java.io.FileWriter(itrFilePathName);
			fw.write(xmlString);
			fw.close();

		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void convertMidiToWavFile(String inputMidiFile, String outputWavFile, boolean dontCreateDrums)
			throws IOException, InvalidMidiDataException {
		if (dontCreateDrums == false) {
			convertMidiToWavFile(inputMidiFile, outputWavFile);
			return;
		}

		// createDrums == false;

		Sequence sequence2 = MidiSystem.getSequence(midiFile);

		// for Drums
		for (int i = 0; i < sequence2.getTracks()[numTrackDrums].size(); ++i) {
			if (sequence2.getTracks()[numTrackDrums].get(i).getMessage() instanceof ShortMessage) {
				ShortMessage s = (ShortMessage) (sequence2.getTracks()[numTrackDrums].get(i).getMessage());
				if (s.getChannel() == 9) {
					if (s.getCommand() == ShortMessage.NOTE_ON) {
						s.setMessage(s.getStatus(), s.getData1(), 0);
					}
				}
			}
		}

		File tempMidFile = File.createTempFile("createTempWithoutDrums", ".mid");
		try {
			MidiSystem.write(sequence2, midiFileFormat.getType(), tempMidFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("createMidiFile done");
		// now convert it to wav format
		convertMidiToWavFile(tempMidFile.getAbsolutePath().toString(), outputWavFile);
		System.out.println(tempMidFile.getAbsolutePath());
		tempMidFile.delete();
	}

	public void convertMidiToWavFile(String inputMidiFile, String outputWavFile) throws IOException {

		ProcessBuilder builder = null;
		// create BGM folder if it's not there
		(new File(outputWavFile).getParentFile().getParentFile()).mkdir();
		(new File(outputWavFile).getParentFile()).mkdir();
		System.out.println("convert midi to wav");
		String timidityPath = new File("dependencies\\TiMidity++-2.14.0").getAbsolutePath();

		if (isWindows()) {
			// relative path Windows
			// "cmd.exe", "/c", "CD " + timidityPath, "&",
			builder = new ProcessBuilder(timidityPath + File.separator + "timidity.exe", inputMidiFile, "-Ow", "-o",
					outputWavFile);

		}
		// different on a Mac
		if (isMacOSX()) {
			builder = new ProcessBuilder(timidityPath + "/mac/timidity/timidity", inputMidiFile, "-Ow", "-o",
					outputWavFile);
		}
		builder.directory(new File(timidityPath));
		builder.redirectErrorStream(true);
		Process p = builder.start();
		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while (true) {
			line = r.readLine();
			if (line == null) {
				break;
			}
			System.out.println(line);
		}
	}

	/**
	 * Returns <code>true</code> if current operating is Windows.
	 */
	public static boolean isWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}

	/**
	 * Returns <code>true</code> if current operating is Mac OS X.
	 */
	public static boolean isMacOSX() {
		return System.getProperty("os.name").startsWith("Mac OS X");
	}

	public void sequencerStart() {
		// sequencer.setTrackSolo(numTrackDrums, true);
		// TODO sequencer.setTrackMute(numTrackDrums, true);
		// sequencer.getSequence().
		sequencer.start();

	}

	public void sequencerStop() {
		sequencer.stop();
	}
}
