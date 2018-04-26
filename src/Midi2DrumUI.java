// Copyright (c) 2018 The Midi2Drum developers
// Distributed under the MIT software license, see the accompanying
// file COPYING or http://www.opensource.org/licenses/mit-license.php.

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Button;
import java.awt.TextField;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.awt.event.ActionEvent;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;

import midi2drum.MidiFileFilter;
import midi2drum.MidiToITR;
import midi2drum.PictureFileFilter;

import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.TextArea;
import java.awt.Color;
import javax.swing.SwingConstants;

public class Midi2DrumUI {

	private JFrame frmMidiToIntotherhythmVRSongGenerator;
	private TextField tf_songTitle;
	private TextField tf_artistName;
	private JLabel lblArtistName;
	private JLabel lblDrumIsOn;
	private TextField tf_drumTrack;
	private TextField tf_midiFile;
	private JFileChooser fc;
	private File midiFile;
	private File pictureFile;
	private MidiToITR midiToITR;
	private JButton buttonPlay;
	private JButton buttonStop;
	private JButton buttonLoadMidi;
	private JButton buttonExport;
	private TextArea textAreaLog;
	private JLabel lblPicture;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Midi2DrumUI window = new Midi2DrumUI();
					window.frmMidiToIntotherhythmVRSongGenerator.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Midi2DrumUI() {
		initialize();
		// setToTestValues();
	}

	@SuppressWarnings("unused")
	private void setToTestValues() {
		tf_drumTrack.setText("1");
		tf_songTitle.setText("Test_Song");
		tf_artistName.setText("Test_ArtistName");
	}

	private void initializeMidiToITR() {
		textAreaLog.append(midiToITR.getInformationMidiFile());
		textAreaLog.append(midiToITR.getMidiFileInformation());
		int drumTrack = midiToITR.getNumTrackDrums();
		// textAreaLog.appendText( midiToITR.getInformationMidiFile());
		if (drumTrack >= 0) {
			tf_drumTrack.setText(Integer.toString(drumTrack));
			textAreaLog.append("Drums Track detected on Track: " + Integer.toString(drumTrack) + "\n");
			
		} else {
			tf_drumTrack.setText(Integer.toString(drumTrack));
			Color color_temp = textAreaLog.getForeground();
			textAreaLog.setForeground(Color.RED);
			textAreaLog.append("Drums Track could not been detected automatically\n");
			textAreaLog.setForeground(color_temp);
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("unchecked")
	private void initialize() {
		frmMidiToIntotherhythmVRSongGenerator = new JFrame();
		frmMidiToIntotherhythmVRSongGenerator.setIconImage(Toolkit.getDefaultToolkit().getImage("resources\\icons\\drumsticks3.png"));
		frmMidiToIntotherhythmVRSongGenerator.setTitle("Midi2Drum -  Song Generator");
		frmMidiToIntotherhythmVRSongGenerator.setBounds(100, 100, 795, 659);
		frmMidiToIntotherhythmVRSongGenerator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMidiToIntotherhythmVRSongGenerator.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), null));
		panel.setBounds(10, 103, 532, 140);
		frmMidiToIntotherhythmVRSongGenerator.getContentPane().add(panel);
		panel.setLayout(null);

		JLabel lblSongTitle = new JLabel("Song Title");
		lblSongTitle.setBounds(10, 17, 63, 14);
		panel.add(lblSongTitle);

		tf_songTitle = new TextField();
		tf_songTitle.setBounds(141, 10, 376, 20);
		panel.add(tf_songTitle);
		tf_songTitle.setColumns(10);

		tf_artistName = new TextField();
		tf_artistName.setColumns(10);
		tf_artistName.setBounds(141, 41, 252, 20);
		panel.add(tf_artistName);

		lblArtistName = new JLabel("Artist Name");
		lblArtistName.setBounds(10, 48, 74, 14);
		panel.add(lblArtistName);

		lblDrumIsOn = new JLabel("Drums are on Track");
		lblDrumIsOn.setBounds(10, 104, 125, 14);
		panel.add(lblDrumIsOn);

		tf_drumTrack = new TextField();
		tf_drumTrack.setBounds(141, 97, 22, 20);
		panel.add(tf_drumTrack);

		JLabel lblLevel = new JLabel("Level");
		lblLevel.setBounds(10, 73, 35, 14);
		panel.add(lblLevel);

		Integer[] comboLevel = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
		@SuppressWarnings("rawtypes")
		JComboBox comboBox = new JComboBox(comboLevel);
		// set standard level to 5
		comboBox.setSelectedIndex(4);
		comboBox.setBounds(55, 69, 57, 22);
		panel.add(comboBox);

		textAreaLog = new TextArea();
		textAreaLog.setForeground(Color.BLACK);
		textAreaLog.setEditable(false);
		textAreaLog.setBounds(10, 313, 742, 160);
		frmMidiToIntotherhythmVRSongGenerator.getContentPane().add(textAreaLog);

		buttonLoadMidi = new JButton("load Midi File...");
		buttonLoadMidi.setFont(new Font("Dialog", Font.PLAIN, 12));
		buttonLoadMidi.setActionCommand("");
		buttonLoadMidi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (fc == null) {
					// jump directly to the installation folder
					fc = new JFileChooser((new File("test.txt")).getAbsoluteFile().getParent());
					// fc.setCurrentDirectory(test.toString());
					fc.addChoosableFileFilter(new MidiFileFilter());
					fc.setAcceptAllFileFilterUsed(false);
				}
				// Show it.
				// int returnVal = fc.showDialog(this, arg1);
				int returnVal = fc.showOpenDialog(null);

				// //Process the results.
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					midiFile = fc.getSelectedFile();
					System.out.println("Attaching file: " + midiFile.getName().toString() + ".");
					tf_midiFile.setText(midiFile.getName().toString());
					try {
						if (midiToITR == null) {
							midiToITR = new MidiToITR(midiFile);
						} else {
							midiToITR.parseMidi(midiFile);
						}
					} catch (MidiUnavailableException | InvalidMidiDataException | IOException e ) {
						textAreaLog.append("ERROR while parsing file:\n" + e.getMessage());
						return;
					}
				} else {
					System.err.println("Attachment cancelled by user.");
				}

				// Reset the file chooser for the next time it's shown.
				fc.setSelectedFile(null);
				fc = null;
				textAreaLog.setText("");
				if (midiFile != null) {
					initializeMidiToITR();
					enablePlay();
				}
			}
		});
		buttonLoadMidi.setBounds(10, 27, 93, 22);
		frmMidiToIntotherhythmVRSongGenerator.getContentPane().add(buttonLoadMidi);

		tf_midiFile = new TextField();
		tf_midiFile.setEditable(false);
		tf_midiFile.setColumns(10);
		tf_midiFile.setBounds(123, 29, 363, 20);
		frmMidiToIntotherhythmVRSongGenerator.getContentPane().add(tf_midiFile);

		buttonPlay = new JButton("play");
		buttonPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				midiToITR.sequencerStart();
				enableStop();
			}
		});
		buttonPlay.setActionCommand("");
		buttonPlay.setEnabled(false);
		buttonPlay.setBounds(10, 64, 41, 22);
		frmMidiToIntotherhythmVRSongGenerator.getContentPane().add(buttonPlay);

		buttonStop = new JButton("stop");
		buttonStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				midiToITR.sequencerStop();
				enablePlay();
			}
		});
		buttonStop.setEnabled(false);
		buttonStop.setActionCommand("");
		buttonStop.setBounds(62, 64, 41, 22);
		frmMidiToIntotherhythmVRSongGenerator.getContentPane().add(buttonStop);

		JButton buttonLoadAlbumCoverPhoto = new JButton("load Album Cover Picture...");
		buttonLoadAlbumCoverPhoto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (fc == null) {
					// jump directly to the installation folder
					fc = new JFileChooser((new File("test.txt")).getAbsoluteFile().getParent());
					// fc.setCurrentDirectory(test.toString());
					fc.addChoosableFileFilter(new PictureFileFilter());
					fc.setAcceptAllFileFilterUsed(false);
				}
				// Show it.
				// int returnVal = fc.showDialog(this, arg1);
				int returnVal = fc.showOpenDialog(null);

				// //Process the results.
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					pictureFile = fc.getSelectedFile();
					System.out.println("Album pictureFile: " + pictureFile.getName().toString() + ".");
					BufferedImage img = null;
					try {
						img = ImageIO.read(pictureFile);
						Image dimg = img.getScaledInstance(lblPicture.getWidth(), lblPicture.getHeight(),
								Image.SCALE_AREA_AVERAGING);
						ImageIcon imageIcon = new ImageIcon(dimg);
						lblPicture.setIcon(imageIcon);

					} catch (IOException e) {
						e.printStackTrace();
					}

				} else {
					System.err.println("Open AlbumCoverPicture cancelled by user.");
				}

				// Reset the file chooser for the next time it's shown.
				fc.setSelectedFile(null);
				fc = null;
			}
		});
		buttonLoadAlbumCoverPhoto.setFont(new Font("Dialog", Font.PLAIN, 12));
		buttonLoadAlbumCoverPhoto.setActionCommand("");
		buttonLoadAlbumCoverPhoto.setBounds(591, 27, 161, 22);
		frmMidiToIntotherhythmVRSongGenerator.getContentPane().add(buttonLoadAlbumCoverPhoto);

		buttonExport = new JButton("export to Into the Rhythm");
		buttonExport.setEnabled(false);
		buttonExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (Integer.parseInt(tf_drumTrack.getText()) != midiToITR.getNumTrackDrums()) {
					midiToITR.setNumTrackDrums(Integer.parseInt(tf_drumTrack.getText()));
					textAreaLog.append("Drums manually set to:" + midiToITR.getNumTrackDrums());
				}

				String outputPath = new File("output").getAbsolutePath();
				
				if (tf_songTitle.getText().length() == 0)
				{
					tf_songTitle.setText("NoTitleName");
				}
				if (tf_artistName.getText().length() == 0)
				{
					tf_artistName.setText("NoArtistName");
				}
				if (pictureFile == null) {
					pictureFile = new File("resources" + File.separator + "coverAlbum" + File.separator + "alt-antik-antiquitat-164899.jpg");
					BufferedImage img = null;
					try {
						img = ImageIO.read(pictureFile);
						Image dimg = img.getScaledInstance(lblPicture.getWidth(), lblPicture.getHeight(),
								Image.SCALE_AREA_AVERAGING);
						ImageIcon imageIcon = new ImageIcon(dimg);
						lblPicture.setIcon(imageIcon);

					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				midiToITR.convertTrackToITR(outputPath + File.separator + tf_songTitle.getText() + ".itr",
						tf_artistName.getText(), tf_songTitle.getText(), "automatic generated by MIDI2DRUM",
						(int) comboBox.getSelectedItem(), pictureFile.getAbsolutePath(), midiToITR.getNumTrackDrums(),
						1);

				try {
					textAreaLog.append("...generating wav files this could take some time (more than 1 minute)");
					midiToITR.convertMidiToWavFile(midiFile.getAbsolutePath(),
							outputPath + File.separator + "BGM" + File.separator + tf_songTitle.getText() + ".wav");
					textAreaLog.append("convertion completed:\n" + outputPath + File.separator
							+ tf_songTitle.getText() + ".itr\n was generated\n");
					midiToITR.createSingleNoteMidi(midiFile.getAbsolutePath(), 
							midiToITR.getNumTrackDrums(), tf_songTitle.getText());
								
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		buttonExport.setBounds(10, 256, 142, 22);
		frmMidiToIntotherhythmVRSongGenerator.getContentPane().add(buttonExport);

		lblPicture = new JLabel("picture");
		lblPicture.setHorizontalAlignment(SwingConstants.CENTER);
		lblPicture.setIcon(null);
		lblPicture.setBounds(552, 64, 213, 214);
		frmMidiToIntotherhythmVRSongGenerator.getContentPane().add(lblPicture);
	}

	private void enablePlay() {
		buttonPlay.setEnabled(true);
		buttonStop.setEnabled(false);
		buttonLoadMidi.setEnabled(true);
		buttonExport.setEnabled(true);
	}

	private void enableStop() {
		buttonPlay.setEnabled(false);
		buttonStop.setEnabled(true);
		buttonLoadMidi.setEnabled(false);
		buttonExport.setEnabled(false);
	}
}
