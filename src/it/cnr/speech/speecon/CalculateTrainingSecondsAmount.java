package it.cnr.speech.speecon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import it.cnr.speech.audio.AudioBits;
import it.cnr.speech.conversions.Conversions;
import it.cnr.speech.features.MfccExtraction;
import it.cnr.speech.fhmm.abla.BaumWelchLearner;
import it.cnr.speech.fhmm.abla.FHMM;
import it.cnr.speech.fhmm.abla.FHMMTrainer;
import it.cnr.speech.fhmm.abla.Hmm;

public class CalculateTrainingSecondsAmount {

	
		
		public static void main(String[] args) throws Exception{
			
			File trainingFolder = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon syllables\\train\\");
			scanAll(trainingFolder);
		}
		
		
		public static void scanAll (File trainingFolder) throws Exception{
			
			File [] allFolders = trainingFolder.listFiles();
			double allSeconds = 0;
			
			for (File folder:allFolders){
				if (folder.isDirectory()){
					File [] allfiles = folder.listFiles();
					for (File f:allfiles) {
						if (f.getName().endsWith(".wav")) {
							
							AudioBits audio = new AudioBits(f);
							short[] shortaudio = audio.getShortVectorAudio();
							AudioFormat af = audio.getAudioFormat();

							float sf = af.getSampleRate();
							
							double seconds = Conversions.samples2Milliseconds(sf, shortaudio.length);

							audio.deallocateAudio();
							
							allSeconds+=seconds;
							
						}
					}
				}
			}
			
			System.out.println("Overall seconds: "+allSeconds);
			
		}
		
		
		
		

}
