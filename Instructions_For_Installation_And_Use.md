
If you don’t have the VR Game program, you have to download it. 
( https://store.steampowered.com/app/677060/Into_the_Rhythm_VR/ )

1.
Go to the GitHub Project “Midi2Drum” (https://github.com/MomoVCHH/Midi2Drum),and download or clone it. 
If you download it then you have to extract it. 

2.
Now double click to open the folder “Midi2Drum-master. ” And then open the src folder and double-click the file “ 
create_bin.bat” to compile the Java Source code the gameFiles folder. 

3.
Go to folder “gameFiles”, and copy all files and folders to your installation path of Into The Rhythm VR. 
(for me it is the path: “C:\Program Files (x86)\Steam\steamapps\common\Into the Rhythm”.

4.
Download the TiMidity++-2.14.0. (https://sourceforge.net/projects/timidity/) and copy the folder TiMidity++-2.14.0 into 
the dependencies folder (“C:\Program Files (x86)\Steam\steamapps\common\Into the Rhythm\dependencies” ). 
Then go to the folder ” Into the Rhythm” and double-click the file”Midi2DrumUI.bat” to start the Java program.
You will get the User Interface which allows you to convert a MIDI File with drums into the format needed for the game.

5.
Now you load the MIDI file and optional an image as the album-cover into the program. 
Type the wished song title (<YourSongTitle>) and artist name, choose a difficulty and optional make other settings. 
You can also press the play button to hear the MIDI Music to test your file. 
After done all the settings you can finally export the files (this could last longer than 1 minute). 
The Drum Instrument (which has always the MIDI Channel 10) will be translated to the ITR format for the gameplay. 
Timidity is used to create a wav File for the Backgroundmusic (BGM) and every single tone of the drums 
(“C:\Program Files (x86)\Steam\steamapps\common\Into the Rhythm\Editor\Note Sound\CustermorNoteSound\<YourSongTitle>)
 
6.
Now you have automatically all the exported files in the subfolders of the folder “Editor” and you can directly use it 
in your gameplay. (The subfolder names of the Editor folder for the generated files are: Image, BGM, ITR 
and \Note Sound\CustermorNoteSound\<YourSongTitle>)

If you open the subfolder CustomNoteSound, you have all the single wav files for the each tone of the drum kit 
in the folder for the song (they were also generated out of the MIDI File) 

7.
Now open the VR Game “Into The Rhythm VR”. There are some options, choose the option “GAME START” and hit the drum “ok”.  
And them choose the option “Play Custom”. There you will find the newly generated songs. 

Now you can see your project on the menu. Enjoy it ! 

Here is the link to the video tutorial 
(https://www.youtube.com/watch?v=J74XRFN-LnY&t=6s&list=PLpo0J_cVkdXNd43MfINbEjMFVRPMublZU&index=2)


Hint: You can also use the generated ITR files in the Editor which comes with Into The Rhythm (IntoTheRhythmEditorTool.exe). The BGM is in wav format. If you like to put a little bit more effort you can try to change the wav file (which is only instrumental in MIDI Files) with a real wav file.
Special Thanks:
Thanks to the developer of “Into The Rhythm VR” which have programmed a great Rhythm VR Game. It was also very helpful to have the Editor environment from the game to get the idea how to autogenerate content. If you want to integrate some of the coding, feel free it’s under MIT License.
